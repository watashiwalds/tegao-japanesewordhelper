import os
import shutil
import zipfile
import json
import requests
from tqdm import tqdm

JMDICT_API_URL = "https://api.github.com/repos/yomidevs/jmdict-yomitan/releases/latest"
KANJI_API_URL = "https://api.github.com/repos/yomidevs/kanjidic-yomitan/releases/latest"

def get_latest_release_info():
    tag_name = None
    links = {}
    try:
        resp_jm = requests.get(JMDICT_API_URL, timeout=15)
        if resp_jm.status_code == 200:
            data_jm = resp_jm.json()
            tag_name = data_jm.get('tag_name')
            assets_jm = data_jm.get('assets', [])
            for asset in assets_jm:
                name = asset['name'].lower()
                if 'jmdict' in name and 'english' in name and 'examples' not in name:
                    links['jmdict'] = asset['browser_download_url']
                    print(f"Found JMdict URL: {links['jmdict']}")
        else:
            print(f"Lỗi lấy JMdict API: {resp_jm.status_code}")

        resp_kanji = requests.get(KANJI_API_URL, timeout=15)
        if resp_kanji.status_code == 200:
            data_kanji = resp_kanji.json()
            assets_kanji = data_kanji.get('assets', [])
            for asset in assets_kanji:
                name = asset['name'].lower()
                if 'kanjidic' in name and 'english' in name:
                    links['kanjidic'] = asset['browser_download_url']
                    print(f"Found Kanjidic URL: {links['kanjidic']}")
        else:
            print(f"Lỗi lấy Kanjidic API: {resp_kanji.status_code}")

        return tag_name, links

    except Exception as e:
        print(f'Error getting release info: {e}')
        return None, {}

def check_if_update_needed(cur,source_name,new_version):
    cur.execute("SELECT version FROM Sources WHERE source_name = %s", (source_name,))
    row = cur.fetchone()
    if not row:
        print(f"It's not necessary to update {source_name} version {new_version}")
        return True
    current_version = row[0]
    if current_version != new_version:
        print(f"Found new version {new_version} for {source_name}")
        return True
    print(f"{source_name} version {new_version} is up to date")
    return False

def download_file(url,save_path):
    try:
        print(f"Downloading {url}")
        resp = requests.get(url,stream=True)
        total = int(resp.headers.get('content-length', 0))
        with open(save_path, 'wb') as file, tqdm(
            desc= os.path.basename(save_path),
            total= total,
            unit = 'iB',
            unit_scale = True,
            unit_divisor = 1024,
        ) as bar:
            for data in resp.iter_content(chunk_size=1024):
                size = file.write(data)
                bar.update(size)
        return True
    except Exception as e:
        print(f'Error: {e}')
        return False


def extract_zip(zip_path,extract_to):
    if os.path.exists(extract_to):
        shutil.rmtree(extract_to)
    os.makedirs(extract_to)

    print(f"Extracting {os.path.basename(zip_path)}")
    try:
        with zipfile.ZipFile(zip_path, 'r') as z:
            z.extractall(extract_to)
            return True
    except zipfile.BadZipFile as e:
        print(e)
        return False

def update_source_meta(cur, extract_path,forced_version=None):
    index_path = os.path.join(extract_path, "index.json")
    if not os.path.exists(index_path):
        return None
    with open(index_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
        title = data.get('title','Unknown')
        version = forced_version if forced_version else data.get('version','')

        cur.execute("""
            INSERT INTO Sources (source_name, update_date, description, version)
            VALUES (%s, NOW(), %s, %s)
            ON CONFLICT (source_name) DO UPDATE
            SET update_date = NOW(), version = EXCLUDED.version
            RETURNING source_id;
        """, (title, data.get('description',''), version))
        return cur.fetchone()[0]
