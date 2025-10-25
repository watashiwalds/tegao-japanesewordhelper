import sys
import pandas as pd
from db_config import get_connection
import re
from jmdict_etl import insert_example
from tqdm import tqdm

def get_or_create_source(cursor, name, update_date=None, description=None, version=None):
    cursor.execute("""
        INSERT INTO Sources (source_name, update_date, description, version)
        VALUES (%s, %s, %s, %s)
        ON CONFLICT (source_name) DO UPDATE
            SET update_date = COALESCE(EXCLUDED.update_date, Sources.update_date),
                description = COALESCE(EXCLUDED.description, Sources.description),
                version = COALESCE(EXCLUDED.version, Sources.version)
        RETURNING source_id
    """, (name, update_date, description, version))
    return cursor.fetchone()[0]

def load_tatoeba(tsv_path):
    conn = get_connection()
    if not conn:
        print("Cannot proceed: Database connection failed.")
        return

    cur = conn.cursor()
    src_id = get_or_create_source(cur, 'Tatoeba', update_date=None, description='Tatoeba sentences', version=None)
    conn.commit()

    try:
        df = pd.read_csv(tsv_path, sep='\t', header=None, names=['id_jp', 'sentence_jp', 'id_en', 'sentence_en'],
                         on_bad_lines='skip')
    except Exception as e:
        print(f"Error loading TSV file: {e}")
        sys.exit(1)
    print(f"Loaded {len(df)} rows from TSV.")

    df_pairs = df[['sentence_jp', 'sentence_en']].rename(columns={'sentence_jp': 'jp', 'sentence_en': 'en'})
    print(f"Found {len(df_pairs)} jp-en pairs.")

    cur.execute("SELECT word_id, primary_reading, primary_writing FROM Words")
    rows = cur.fetchall()
    word_index = {}
    for wid, read, write in rows:
        if write:
            word_index.setdefault(write, []).append(wid)
        if read and read != write:
            word_index.setdefault(read, []).append(wid)
    print(f"Loaded {len(word_index)} unique words from Words table.")
    if not word_index:
        print("Warning: No words found in Words table, attempting to proceed anyway.")

    cur.execute("SELECT sense_id, word_id FROM Senses")
    senses = cur.fetchall()
    word_to_senses = {}
    for sense_id, word_id in senses:
        word_to_senses.setdefault(word_id, []).append(sense_id)
    print(f"Loaded {len(senses)} senses from Senses table.")

    pbar = tqdm(total=len(df_pairs), desc="Loading Tatoeba")
    inserted_examples = 0
    for idx, row in df_pairs.iterrows():
        jp = str(row['jp']) if 'jp' in row else ''
        en = str(row['en']) if 'en' in row else None

        matched_word_ids = set()
        for word, wids in word_index.items():
            if re.search(r'\b' + re.escape(word) + r'\b', jp):
                matched_word_ids.update(wids)

        if matched_word_ids:
            print(f"Matched {len(matched_word_ids)} words for sentence: {jp}")

        for matched_word_id in matched_word_ids:
            sense_ids = word_to_senses.get(matched_word_id, [])
            if not sense_ids:
                cur.execute("""
                            INSERT INTO Senses (word_id, definition_en, definition_vi, gloss, sense_number)
                            VALUES (%s, %s, %s, %s, %s) RETURNING sense_id
                            """, (matched_word_id, None, None, 'example_inserted', 1))
                sense_id = cur.fetchone()[0]
                word_to_senses.setdefault(matched_word_id, []).append(sense_id)
            else:
                sense_id = sense_ids[0]

            insert_example(cur, sense_id, jp, en)
            inserted_examples += 1
        pbar.update(1)
        if (idx + 1) % 500 == 0:
            conn.commit()
            print(f"Processed {idx + 1} pairs, inserted {inserted_examples} examples...")

    pbar.close()
    conn.commit()
    cur.close()
    conn.close()
    print("Tatoeba load completed.")