import os
import sys
import glob
import json
import zipfile

current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(current_dir)
sys.path.append(root_dir)

DATA_DIR = os.path.join(root_dir, "data")
ZIP_FILE = os.path.join(DATA_DIR, "KANJIDIC_english.zip")


def inspect_json():
    print(f"🔍 Đang kiểm tra file: {ZIP_FILE}")

    if not os.path.exists(ZIP_FILE):
        print("❌ Không tìm thấy file zip KANJIDIC_english.zip trong thư mục data!")
        return

    try:
        with zipfile.ZipFile(ZIP_FILE, 'r') as z:
            # Tìm file kanji_bank
            bank_files = [f for f in z.namelist() if "kanji_bank" in f]
            if not bank_files:
                print("❌ Không tìm thấy file kanji_bank_*.json trong zip.")
                return

            target_file = bank_files[0]
            print(f"📖 Đang đọc file: {target_file}")

            with z.open(target_file) as f:
                data = json.load(f)

                print("\n--- MẪU DỮ LIỆU (5 MỤC ĐẦU TIÊN) ---")
                for i, item in enumerate(data[:5]):
                    print(f"\nItem {i}:")
                    print(f"   - Độ dài list: {len(item)}")
                    print(f"   - Index 0 (Char): {item[0]}")
                    print(f"   - Index 4 (Meaning): {item[4]}")

                    if len(item) > 5:
                        print(f"   - Index 5 (Stats?): {item[5]} <--- CÓ DỮ LIỆU Ở ĐÂY KHÔNG?")
                        print(f"     -> Kiểu dữ liệu: {type(item[5])}")
                    else:
                        print("   - ⚠️ KHÔNG CÓ INDEX 5 (Dữ liệu bị thiếu hoặc cấu trúc khác)")

    except Exception as e:
        print(f"❌ Lỗi: {e}")


if __name__ == "__main__":
    inspect_json()