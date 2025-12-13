import os
import sys
import glob
import json

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from db_config import get_connection
from utils import extract_zip, update_source_meta

TEMP_DIR = "/app/temp_kanji"

def run(zip_file_path, version_tag):
    print(f"[Kanji]Processing {version_tag}")
    conn = get_connection()
    if not conn: return
    conn.autocommit = False
    cur = conn.cursor()

    try:
        if not extract_zip(zip_file_path, TEMP_DIR): return
        source_id = update_source_meta(cur, TEMP_DIR, forced_version=version_tag)
        files = glob.glob(os.path.join(TEMP_DIR,"kanji_bank_*.json"))
        for file_path in files:
            with open(file_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
                for item in data:
                    meanings = item[4]
                    m_str = ", ".join(meanings) if isinstance(meanings, list) else str(meanings)
                    cur.execute("""
                        INSERT INTO Kanji (character, on_reading, kun_reading, meaning)
                        VALUES(%s, %s, %s, %s)
                        ON CONFLICT (character) DO UPDATE
                        SET meaning = EXCLUDED.meaning, on_reading = EXCLUDED.on_reading, kun_reading = EXCLUDED.kun_reading
                    """, (item[0], item[1], item[2], m_str))

        conn.commit()
        print("Kanji done!")
    except Exception as e:
        print(f"[Kanji] Error: {e}")
        conn.rollback()
    finally:
        cur.close()
        conn.close()
        import shutil
        if os.path.exists(TEMP_DIR): shutil.rmtree(TEMP_DIR)