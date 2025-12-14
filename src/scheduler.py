import time
import schedule
import process_kanji
import process_jmdict
import export_to_sqlite
import sys
import os

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
        return local_path, False
    download_path = os.path.join(DOWNLOAD_DIR, filename)
    return download_path, True


def job():
    print("Starting update cycle")
    latest_tag, links = get_latest_release_info()
    if not latest_tag or not links:
        print("Could not get release info")
        return

    print(f"Latest version: {latest_tag}")
    ensure_dir(DOWNLOAD_DIR)

    conn = get_connection()
    if not conn: return
    cur = conn.cursor()

    has_update = False

    try:
        if 'kanjidic' in links:
            if check_if_update_needed(cur, 'KANJIDIC (English)', latest_tag):
                filename = 'KANJIDIC_english.zip'
                target_path, need_download = get_file_path(filename)
                if need_download:
                    print(f"Downloading {filename}")
                    if download_file(links['kanjidic'], target_path):
                        process_kanji.run(target_path, latest_tag)
                        has_update = True
                else:
                    process_kanji.run(target_path, latest_tag)
                    has_update = True

        if 'jmdict' in links:
            if check_if_update_needed(cur, 'JMdict (English)', latest_tag):
                filename = 'JMdict_english.zip'
                target_path, need_download = get_file_path(filename)
                if need_download:
                    print(f"Downloading {filename}")
                    if download_file(links['jmdict'], target_path):
                        process_jmdict.run(target_path, latest_tag)
                        has_update = True
                else:
                    process_jmdict.run(target_path, latest_tag)
                    has_update = True

        if has_update:
            export_to_sqlite.run_export()
        else:
            print("No updates found")

    except Exception as e:
        print(f"Scheduler error: {e}")
    finally:
        cur.close()
        conn.close()
        print("Update cycle finished")


if __name__ == '__main__':
    job()
    schedule.every(30).days.do(job)
    while True:
        schedule.run_pending()
        time.sleep(600)