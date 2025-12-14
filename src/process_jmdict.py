import os
import sys
import glob
import json
import traceback
import psycopg2.extras
import re
import shutil
import unicodedata

current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(current_dir)
sys.path.append(root_dir)

from db_config import get_connection

try:
    from utils import extract_zip, update_source_meta
except ImportError:
    from src.utils import extract_zip, update_source_meta

TEMP_DIR = os.path.join(root_dir, "temp_jmdict")

JP_PATTERN = re.compile(
    r'[\u3000-\u303F\u3040-\u309F\u30A0-\u30FF\u4E00-\u9FFF\u3400-\u4DBF\uF900-\uFAFF\uFF00-\uFFEF]')
GARBAGE_SYMBOLS_PATTERN = re.compile(r'[\u2205\u2234\u2200-\u22FF]')
BLACKLIST_KEYWORDS = ["formtable", "charposition", "onomatopoeic", "imgdr", "kanji repetition mark", "(r)", "(p)",
                      "(uk)", "(ateji)"]


def sanitize_text(text):
    if not text: return ""
    text = unicodedata.normalize('NFKC', text)
    if GARBAGE_SYMBOLS_PATTERN.search(text): return ""
    if re.fullmatch(r'[\s;,Rr]+', text): return ""

    text_lower = text.lower().strip()
    if text_lower in ["(r)", "(p)", "(uk)", "(ateji)"]: return ""
    for kw in BLACKLIST_KEYWORDS:
        if kw in text_lower: return ""

    text = re.sub(r'(?i)\s*(?:info)?glossary\s*[-–—]\s*(?:see|usu|notes)\b.*', '', text)
    text = re.sub(r'(?i);\s*see\s*:.*', '', text)
    text = re.sub(r'(?i)\bsee\s*:\s*;?', '', text)
    text = re.sub(r'(?i)\s*refGlosses.*', '', text)
    text = re.sub(r'(?i)\s*glossary\s*$', '', text)
    text = re.sub(r'\b\d+\.\s+', ' ', text)
    text = JP_PATTERN.sub(' ', text)
    text = re.sub(r'[★☆㊙〃→←↑↓「」『』【】]', ' ', text)
    garbage_words = ["part of speech", "m-sl", "col", "vulg", "fam", "sl", "usu.", "hon."]
    for word in garbage_words:
        text = re.sub(r'(?i)\b' + re.escape(word) + r'\b', ' ', text)
    text = re.sub(r'\s+[-–—]\s+', '; ', text)
    text = re.sub(r'\s*([,;])\s*', r'\1 ', text)
    text = re.sub(r'\(\s*\)', '', text)
    text = re.sub(r'\(\s+', '(', text)
    text = re.sub(r'\s+\)', ')', text)
    text = text.strip(" .,;-")
    text = re.sub(r'\s+', ' ', text)
    if re.fullmatch(r'[\s;,Rr]+', text): return ""
    return text.strip()


def clean_structured_content(content):
    if content is None: return ""
    if isinstance(content, str): return content
    if isinstance(content, list):
        cleaned_list = [clean_structured_content(c) for c in content]
        return "; ".join([c for c in cleaned_list if c.strip()]).strip()
    if isinstance(content, dict):
        c_content = content.get("content")
        c_tag = content.get("tag")
        if isinstance(c_content, str):
            if c_content.lower() in ["glossary", "notes", "formtable", "infoglossary", "literal"]: return ""
        if c_tag == "br": return "; "
        text_parts = []
        if "content" in content:
            text_parts.append(clean_structured_content(content["content"]))
        elif "text" in content:
            text_parts.append(clean_structured_content(content["text"]))
        elif "data" in content:
            data_val = content["data"]
            if isinstance(data_val, str) and len(data_val) < 50 and "{" not in data_val: text_parts.append(data_val)
        return " ".join([t for t in text_parts if t]).strip()
    return ""


def is_valid_definition(text):
    if not text or len(text) < 2: return False
    if not re.search(r'[a-zA-Z]', text): return False
    if re.match(r'^[\(\)\[\]\s,;.-]+$', text): return False
    return True


def process_tag_banks(cur, temp_dir):
    tag_files = glob.glob(os.path.join(temp_dir, "tag_bank_*.json"))
    if not tag_files: return
    for file_path in tag_files:
        with open(file_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
            tags_buffer = []
            for item in data:
                tag_name = item[0]
                description = item[3] if len(item) > 3 else ""
                tags_buffer.append((tag_name, description))
            if tags_buffer:
                psycopg2.extras.execute_values(
                    cur,
                    "INSERT INTO Tags (tag_name, description) VALUES %s ON CONFLICT (tag_name) DO UPDATE SET description = EXCLUDED.description",
                    tags_buffer
                )


def get_tag_map(cur):
    cur.execute("SELECT tag_name, tag_id FROM Tags")
    return {row[0]: row[1] for row in cur.fetchall()}


def run(zip_file_path, version_tag):
    print(f"JMdict process started: {version_tag}")
    conn = get_connection()
    if not conn: return
    conn.autocommit = True
    cur = conn.cursor()

    try:
        if not extract_zip(zip_file_path, TEMP_DIR): return
        source_id = update_source_meta(cur, TEMP_DIR, forced_version=version_tag)
        if isinstance(source_id, tuple): source_id = source_id[0]

        process_tag_banks(cur, TEMP_DIR)
        tag_map = get_tag_map(cur)

        cur.execute("CREATE TABLE IF NOT EXISTS raw_jmdict (raw_id serial primary key, data jsonb);")
        cur.execute("TRUNCATE TABLE raw_jmdict")

        term_files = glob.glob(os.path.join(TEMP_DIR, "term_bank_*.json"))
        term_files.sort()

        print(f"Importing {len(term_files)} JSON files")
        for file_path in term_files:
            with open(file_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
                values = [(json.dumps(item, ensure_ascii=False),) for item in data]
                psycopg2.extras.execute_values(cur, "INSERT INTO raw_jmdict (data) VALUES %s", values, page_size=2000)

        print("Updating Words table")
        cur.execute(f"""
            INSERT INTO Words (primary_writing, primary_reading, source_id, frequency_rank)
            SELECT DISTINCT ON (data->>0, COALESCE(NULLIF(data->>1, ''), data->>0))
                data->>0, COALESCE(NULLIF(data->>1, ''), data->>0),
                {source_id}, COALESCE((data->>4)::int, 0)
            FROM raw_jmdict
            ON CONFLICT (primary_reading, primary_writing) DO UPDATE SET source_id = EXCLUDED.source_id;
        """)

        print("Processing Senses & Tags")
        cur.execute("SELECT primary_writing, primary_reading, word_id FROM Words WHERE source_id = %s", (source_id,))
        word_map = {(row[0], row[1]): row[2] for row in cur.fetchall()}

        senses_buffer = []
        word_tags_buffer = []
        BATCH_SIZE = 5000
        seen_definitions = set()
        seen_word_tags = set()

        for file_path in term_files:
            with open(file_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
                new_tags_found = set()

                for item in data:
                    p_writing = item[0]
                    p_reading = item[1] or p_writing
                    word_id = word_map.get((p_writing, p_reading))
                    if not word_id: continue

                    raw_tags_str = (item[2] or "") + " " + (item[7] or "")
                    for tag_name in raw_tags_str.split():
                        if not tag_name: continue
                        if tag_name not in tag_map:
                            new_tags_found.add(tag_name)
                        else:
                            tag_id = tag_map[tag_name]
                            if (word_id, tag_id) not in seen_word_tags:
                                word_tags_buffer.append((word_id, tag_id))
                                seen_word_tags.add((word_id, tag_id))

                    glossary_list = item[5]
                    valid_sense_count = 0
                    for raw_sense in glossary_list:
                        clean_text = sanitize_text(clean_structured_content(raw_sense))
                        if not is_valid_definition(clean_text): continue

                        dedup_key = (word_id, clean_text)
                        if dedup_key in seen_definitions: continue
                        seen_definitions.add(dedup_key)

                        valid_sense_count += 1
                        senses_buffer.append((word_id, clean_text, valid_sense_count))

                if new_tags_found:
                    psycopg2.extras.execute_values(cur,
                                                   "INSERT INTO Tags (tag_name, description) VALUES %s ON CONFLICT DO NOTHING",
                                                   [(t, None) for t in new_tags_found])

                if len(senses_buffer) >= BATCH_SIZE:
                    psycopg2.extras.execute_values(cur,
                                                   "INSERT INTO Senses (word_id, definition_en, sense_number) VALUES %s",
                                                   senses_buffer)
                    senses_buffer = []
                if len(word_tags_buffer) >= BATCH_SIZE:
                    psycopg2.extras.execute_values(cur,
                                                   "INSERT INTO Word_Tags (word_id, tag_id) VALUES %s ON CONFLICT DO NOTHING",
                                                   word_tags_buffer)
                    word_tags_buffer = []
                    seen_word_tags = set()

        if senses_buffer: psycopg2.extras.execute_values(cur,
                                                         "INSERT INTO Senses (word_id, definition_en, sense_number) VALUES %s",
                                                         senses_buffer)
        if word_tags_buffer: psycopg2.extras.execute_values(cur,
                                                            "INSERT INTO Word_Tags (word_id, tag_id) VALUES %s ON CONFLICT DO NOTHING",
                                                            word_tags_buffer)

        cur.execute("TRUNCATE TABLE raw_jmdict")
        print("JMdict processing finished")

    except Exception as e:
        print(f"JMdict Error: {e}")
        traceback.print_exc()
    finally:
        cur.close()
        conn.close()
        if os.path.exists(TEMP_DIR): shutil.rmtree(TEMP_DIR)