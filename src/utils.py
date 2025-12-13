import os
import shutil
import zipfile
import json
import requests
from tqdm import tqdm

GITHUB_API_URL = "https://api.github.com/repos/yomidevs/jmdict-yomitan/releases/latest"

def get_latest_release_info():
    """Crawl the latest release info from the GitHub API."""
    try:
        resp = requests.get(GITHUB_API_URL, timeout=15)
        if resp.status_code != 200:
            print(f"LỖI API: Status Code = {resp.status_code}")
            print(f"Nội dung trả về: {resp.text[:500]}")  # In 500 ký tự đầu xem là cái gì
            return None, {}
        resp.raise_for_status()
        data = resp.json()

        tag_name = data.get('tag_name')
        assets = data.get('assets',[])

        links = {}
        for asset in assets:
            name = asset['name'].lower()
            url = asset['browser_download_url']
            if 'jmdict' in name and 'english' in name and 'examples' not in name:
                links['jmdict'] = url
            elif 'kanjidic' in name and 'english' in name:
                links['kanjidic'] = url

        return tag_name, links
    except Exception as e:
        print(f'Error: {e}')
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
