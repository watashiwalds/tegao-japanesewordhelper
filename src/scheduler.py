import time
import schedule
import process_kanji
import process_jmdict
import sys
import os
import shutil

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from db_config import get_connection
from utils import get_latest_release_info, check_if_update_needed, download_file


DATA_DIR = "/app/data"
DOWNLOAD_DIR = "/app/downloads"


def ensure_dir(directory):
    if not os.path.exists(directory):
        os.makedirs(directory)


def get_file_path(filename):
    local_path = os.path.join(DATA_DIR, filename)
    if os.path.exists(local_path):
        print(f"Tìm thấy file có sẵn: {local_path}")
        return local_path, False


    download_path = os.path.join(DOWNLOAD_DIR, filename)
    return download_path, True


def job():
    print("\n----- Bắt đầu chu kỳ cập nhật -----")

    latest_tag, links = get_latest_release_info()
    if not latest_tag or not links:
        print("Không lấy được thông tin release mới nhất.")
        return

    print(f"Phiên bản mới nhất: {latest_tag}")
    ensure_dir(DOWNLOAD_DIR)

    conn = get_connection()
    if not conn: return
    cur = conn.cursor()

    try:

        if 'kanjidic' in links:
            if check_if_update_needed(cur, 'KANJIDIC (English)', latest_tag):
                filename = 'KANJIDIC_english.zip'
                target_path, need_download = get_file_path(filename)

                success = True
                if need_download:
                    print(f"Đang tải {filename}...")
                    success = download_file(links['kanjidic'], target_path)

                if success:
                    process_kanji.run(target_path, latest_tag)

        if 'jmdict' in links:

            if check_if_update_needed(cur, 'JMdict (English)', latest_tag):
                filename = 'JMdict_english.zip'
                target_path, need_download = get_file_path(filename)

                success = True
                if need_download:
                    print(f"Đang tải {filename}...")
                    success = download_file(links['jmdict'], target_path)

                if success:
                    process_jmdict.run(target_path, latest_tag)

    except Exception as e:
        print(f"Lỗi Scheduler: {e}")
        import traceback
        traceback.print_exc()
    finally:
        cur.close()
        conn.close()
        print("----- Kết thúc chu kỳ -----")


if __name__ == '__main__':
    job()
    schedule.every(30).days.do(job)
    while True:
        schedule.run_pending()
        time.sleep(600)