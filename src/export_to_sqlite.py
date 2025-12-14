import os
import sys
import sqlite3
import time

current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(current_dir)
sys.path.append(root_dir)

from db_config import get_connection

SQLITE_DB_PATH = os.path.join(root_dir, "data", "dictionary.db")


def create_sqlite_schema(cursor):
    # Xóa bảng cũ nếu có (Bao gồm cả android_metadata nếu file cũ còn sót lại)
    tables = ["Word_Tags", "Senses", "Kanji", "Words", "Tags", "Sources", "android_metadata"]
    for tbl in tables:
        cursor.execute(f"DROP TABLE IF EXISTS {tbl}")

    # 1. Sources
    cursor.execute("""
                   CREATE TABLE Sources
                   (
                       source_id   INTEGER PRIMARY KEY NOT NULL,
                       source_name TEXT,
                       update_date TEXT,
                       description TEXT,
                       version     TEXT
                   )
                   """)

    # 2. Tags
    cursor.execute("""
                   CREATE TABLE Tags
                   (
                       tag_id      INTEGER PRIMARY KEY NOT NULL,
                       tag_name    TEXT,
                       description TEXT
                   )
                   """)

    # 3. Words (Lite Version)
    cursor.execute("""
                   CREATE TABLE Words
                   (
                       word_id         INTEGER PRIMARY KEY NOT NULL,
                       primary_reading TEXT,
                       primary_writing TEXT,
                       frequency_rank  INTEGER,
                       source_id       INTEGER
                   )
                   """)

    # 4. Kanji (Lite Version: 5 columns)
    cursor.execute("""
                   CREATE TABLE Kanji
                   (
                       kanji_id    INTEGER PRIMARY KEY NOT NULL,
                       character   TEXT,
                       on_reading  TEXT,
                       kun_reading TEXT,
                       meaning     TEXT
                   )
                   """)

    # 5. Senses (Lite Version: 4 columns)
    cursor.execute("""
                   CREATE TABLE Senses
                   (
                       sense_id      INTEGER PRIMARY KEY NOT NULL,
                       word_id       INTEGER             NOT NULL,
                       definition_en TEXT,
                       sense_number  INTEGER
                   )
                   """)

    # 6. Word_Tags
    cursor.execute("""
                   CREATE TABLE Word_Tags
                   (
                       word_tag_id INTEGER PRIMARY KEY NOT NULL,
                       word_id     INTEGER             NOT NULL,
                       tag_id      INTEGER             NOT NULL
                   )
                   """)

    # ĐÃ XÓA BẢNG android_metadata TẠI ĐÂY


def copy_table(pg_conn, sqlite_conn, table_name, columns):
    pg_cur = pg_conn.cursor()
    try:
        pg_cur.execute(f"SELECT COUNT(*) FROM {table_name}")
        if pg_cur.fetchone()[0] == 0:
            return

        col_str = ", ".join(columns)
        pg_cur.execute(f"SELECT {col_str} FROM {table_name}")
        rows = pg_cur.fetchall()

        sqlite_cur = sqlite_conn.cursor()
        placeholders = ",".join(["?"] * len(columns))
        sql = f"INSERT INTO {table_name} ({col_str}) VALUES ({placeholders})"

        sqlite_cur.executemany(sql, rows)
        sqlite_conn.commit()
        print(f"Copied {table_name}: {len(rows)} rows")

    except Exception as e:
        print(f"Error copying {table_name}: {e}")
    finally:
        pg_cur.close()


def create_indices(cursor):
    idx_sql = [
        "CREATE INDEX idx_words_read ON Words(primary_reading)",
        "CREATE INDEX idx_words_write ON Words(primary_writing)",
        "CREATE INDEX idx_kanji_char ON Kanji(character)",
        "CREATE INDEX idx_senses_wid ON Senses(word_id)",
        "CREATE INDEX idx_wt_wid ON Word_Tags(word_id)",
        "CREATE INDEX idx_wt_tid ON Word_Tags(tag_id)"
    ]
    for sql in idx_sql:
        try:
            cursor.execute(sql)
        except:
            pass


def run_export():
    print("Starting SQLite export (Clean Version - No Metadata)")

    # Bắt buộc xóa file cũ để đảm bảo không còn tàn dư của bảng android_metadata
    if os.path.exists(SQLITE_DB_PATH):
        try:
            os.remove(SQLITE_DB_PATH)
        except:
            pass

    pg_conn = get_connection()
    if not pg_conn:
        print("Postgres connection failed")
        return

    sqlite_conn = sqlite3.connect(SQLITE_DB_PATH)
    sqlite_cur = sqlite_conn.cursor()
    sqlite_cur.execute("PRAGMA synchronous = OFF")
    sqlite_cur.execute("PRAGMA journal_mode = MEMORY")

    try:
        create_sqlite_schema(sqlite_cur)
        sqlite_conn.commit()

        copy_table(pg_conn, sqlite_conn, "Sources",
                   ["source_id", "source_name", "update_date", "description", "version"])
        copy_table(pg_conn, sqlite_conn, "Tags", ["tag_id", "tag_name", "description"])
        copy_table(pg_conn, sqlite_conn, "Words",
                   ["word_id", "primary_reading", "primary_writing", "frequency_rank", "source_id"])
        copy_table(pg_conn, sqlite_conn, "Kanji", ["kanji_id", "character", "on_reading", "kun_reading", "meaning"])
        copy_table(pg_conn, sqlite_conn, "Senses", ["sense_id", "word_id", "definition_en", "sense_number"])
        copy_table(pg_conn, sqlite_conn, "Word_Tags", ["word_tag_id", "word_id", "tag_id"])

        create_indices(sqlite_cur)
        sqlite_conn.commit()
        sqlite_cur.execute("VACUUM")

        print(f"Export finished. File: {SQLITE_DB_PATH}")

    except Exception as e:
        print(f"Export failed: {e}")
    finally:
        sqlite_conn.close()
        pg_conn.close()

if __name__ == "__main__":
    run_export()