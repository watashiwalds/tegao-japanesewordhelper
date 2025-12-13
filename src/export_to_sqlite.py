import os
import sys
import sqlite3
import psycopg2
import time

# --- SETUP ĐƯỜNG DẪN ---
current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(current_dir)
sys.path.append(root_dir)

from db_config import get_connection

SQLITE_DB_PATH = os.path.join(root_dir, "data", "dictionary.db")


def create_sqlite_schema(cursor):
    print("📦 Đang tạo bảng (Schema)...")
    tables = ["Cross_references", "Word_Tags", "Examples", "Senses", "Kanji", "Words", "Tags", "Sources",
              "android_metadata"]
    for tbl in tables:
        cursor.execute(f"DROP TABLE IF EXISTS {tbl}")

    cursor.execute(
        "CREATE TABLE Sources (source_id INTEGER PRIMARY KEY, source_name TEXT, update_date TEXT, description TEXT, version TEXT)")
    cursor.execute("CREATE TABLE Tags (tag_id INTEGER PRIMARY KEY, tag_name TEXT, description TEXT)")
    cursor.execute(
        "CREATE TABLE Words (word_id INTEGER PRIMARY KEY, primary_reading TEXT, primary_writing TEXT, part_of_speech TEXT, frequency_rank INTEGER, source_id INTEGER)")
    cursor.execute(
        "CREATE TABLE Kanji (kanji_id INTEGER PRIMARY KEY, character TEXT, stroke_count INTEGER, radical TEXT, on_reading TEXT, kun_reading TEXT, meaning TEXT, grade_level INTEGER, jlpt_level INTEGER, frequency INTEGER)")
    cursor.execute(
        "CREATE TABLE Senses (sense_id INTEGER PRIMARY KEY, word_id INTEGER, definition_en TEXT, definition_vi TEXT, gloss TEXT, sense_number INTEGER)")
    cursor.execute(
        "CREATE TABLE Examples (example_id INTEGER PRIMARY KEY, sense_id INTEGER, sentence_jp TEXT, sentence_en TEXT)")
    cursor.execute("CREATE TABLE Word_Tags (word_tag_id INTEGER PRIMARY KEY, word_id INTEGER, tag_id INTEGER)")
    cursor.execute(
        "CREATE TABLE Cross_references (ref_id INTEGER PRIMARY KEY, word_id INTEGER, related_word_id INTEGER, relation_type TEXT)")
    cursor.execute("CREATE TABLE android_metadata (locale TEXT)")
    cursor.execute("INSERT INTO android_metadata VALUES ('en_US')")


def brutal_copy(pg_conn, sqlite_conn, table_name, columns):
    """
    Copy kiểu 'Vét cạn': Lấy hết dữ liệu Postgres 1 lần rồi ném sang SQLite.
    Không dùng server-side cursor để tránh lỗi ảo.
    """
    print(f"\n🔄 Đang xử lý bảng: [{table_name}]")

    # 1. Kiểm tra nguồn Postgres
    pg_cur = pg_conn.cursor()
    try:
        count_query = f"SELECT COUNT(*) FROM {table_name}"
        pg_cur.execute(count_query)
        pg_count = pg_cur.fetchone()[0]
    except Exception as e:
        print(f"   ❌ Lỗi đếm dòng Postgres: {e}")
        return

    print(f"   📊 Nguồn Postgres có: {pg_count:,} dòng.")

    if pg_count == 0:
        print("   ⚠️ Bảng nguồn trống -> Bỏ qua.")
        return

    # 2. Lấy toàn bộ dữ liệu (Fetch All)
    print("   ⬇️  Đang tải dữ liệu từ Postgres về RAM...")
    start = time.time()
    col_str = ", ".join(columns)
    pg_cur.execute(f"SELECT {col_str} FROM {table_name}")
    rows = pg_cur.fetchall()  # Lấy hết sạch sành sanh
    print(f"   ✅ Đã tải xong {len(rows):,} dòng trong {time.time() - start:.2f}s")

    # 3. Ghi vào SQLite
    print("   ⬆️  Đang ghi vào SQLite...")
    sqlite_cur = sqlite_conn.cursor()
    placeholders = ",".join(["?"] * len(columns))
    sql = f"INSERT INTO {table_name} ({col_str}) VALUES ({placeholders})"

    try:
        sqlite_cur.executemany(sql, rows)
        sqlite_conn.commit()  # <--- COMMIT NGAY LẬP TỨC
        print(f"   💾 Đã lưu thành công bảng {table_name}.")
    except Exception as e:
        print(f"   ❌ Lỗi khi Insert SQLite: {e}")

    pg_cur.close()


def create_indices(cursor):
    print("\n⚡ Đang tạo Index...")
    idx_sql = [
        "CREATE INDEX idx_words_read ON Words(primary_reading)",
        "CREATE INDEX idx_words_write ON Words(primary_writing)",
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
    print(f"🚀 BẮT ĐẦU EXPORT (CHẾ ĐỘ BRUTAL FORCE)")

    # Kết nối Postgres
    pg_conn = get_connection()
    if not pg_conn:
        print("❌ Không kết nối được Postgres. Kiểm tra db_config.py")
        return

    # In ra ta đang kết nối vào đâu để chắc chắn không nhầm DB
    print(f"🔌 Kết nối Postgres: {pg_conn.dsn}")

    # Xóa file cũ & Kết nối SQLite
    if os.path.exists(SQLITE_DB_PATH):
        try:
            os.remove(SQLITE_DB_PATH)
        except:
            pass

    sqlite_conn = sqlite3.connect(SQLITE_DB_PATH)
    sqlite_cur = sqlite_conn.cursor()
    sqlite_cur.execute("PRAGMA synchronous = OFF")  # Tăng tốc

    try:
        # 1. Tạo bảng
        create_sqlite_schema(sqlite_cur)
        sqlite_conn.commit()

        # 2. Copy từng bảng (Thứ tự quan trọng)
        # Copy Sources
        brutal_copy(pg_conn, sqlite_conn, "Sources",
                    ["source_id", "source_name", "update_date", "description", "version"])

        # Copy Tags
        brutal_copy(pg_conn, sqlite_conn, "Tags",
                    ["tag_id", "tag_name", "description"])

        # Copy Words
        brutal_copy(pg_conn, sqlite_conn, "Words",
                    ["word_id", "primary_reading", "primary_writing", "part_of_speech", "frequency_rank", "source_id"])

        # Copy Senses
        brutal_copy(pg_conn, sqlite_conn, "Senses",
                    ["sense_id", "word_id", "definition_en", "definition_vi", "gloss", "sense_number"])

        # Copy Word_Tags
        brutal_copy(pg_conn, sqlite_conn, "Word_Tags",
                    ["word_tag_id", "word_id", "tag_id"])

        # 3. Index & Vacuum
        create_indices(sqlite_cur)
        sqlite_conn.commit()
        sqlite_cur.execute("VACUUM")

        print(f"\n✅✅✅ XONG! Kiểm tra file: {SQLITE_DB_PATH}")

    except Exception as e:
        print(f"❌ Lỗi TỔNG: {e}")
    finally:
        sqlite_conn.close()
        pg_conn.close()


if __name__ == "__main__":
    run_export()