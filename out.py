import struct
import numpy as np
from PIL import Image
import os

# Kích thước ảnh
IMG_WIDTH, IMG_HEIGHT = 128, 127
RECORD_SIZE = 8199

def read_etl9g(filename, max_records=None):
    records = []
    with open(filename, 'rb') as f:
        i = 0
        while True:
            raw = f.read(RECORD_SIZE)
            if not raw or (max_records and i >= max_records):
                break

            # --- Metadata ---
            serial_sheet_no = struct.unpack(">H", raw[0:2])[0]
            jis_code = struct.unpack(">H", raw[2:4])[0]
            reading = raw[4:12].decode('ascii', errors='ignore').strip()
            serial_data_no = struct.unpack(">I", raw[12:16])[0]
            gender_code = raw[18]
            age = raw[19]

            # --- Image data ---
            img_bytes = raw[64:64 + 8128]
            img = np.zeros((IMG_HEIGHT, IMG_WIDTH), dtype=np.uint8)

            pixels = []
            for b in img_bytes:
                high = (b >> 4) & 0x0F
                low = b & 0x0F
                pixels.extend([high, low])
            img = np.array(pixels[:IMG_WIDTH * IMG_HEIGHT], dtype=np.uint8).reshape((IMG_HEIGHT, IMG_WIDTH))
            img = (15 - img) * 17

            records.append({
                "sheet_no": serial_sheet_no,
                "jis_code": jis_code,
                "reading": reading,
                "serial_data_no": serial_data_no,
                "gender": gender_code,
                "age": age,
                "image": img
            })
            i += 1
    return records

# Hàm lưu ảnh ra thư mục
def save_images_to_folder(records, output_folder=r"D:\PycharmProjects\NLP\Out"):
    for idx, record in enumerate(records):
        jis_folder = os.path.join(output_folder, str(record['jis_code']))
        if not os.path.exists(jis_folder):
            os.makedirs(jis_folder)

        img = record["image"]
        base_filename = f"image_{record['serial_data_no']}_jis_{record['jis_code']}"
        filename = base_filename + ".png"
        filepath = os.path.join(jis_folder, filename)

        counter = 1
        while os.path.exists(filepath):
            filename = f"{base_filename}_{counter}.png"
            filepath = os.path.join(jis_folder, filename)
            counter += 1

        img_pil = Image.fromarray(img)
        img_pil.save(filepath)
        print(f"Đã lưu ảnh: {filepath}")

# Đường dẫn đến thư mục chứa dữ liệu ETL9G
data_dir = r"D:\PycharmProjects\NLP\ETL9G"
output_folder = r"D:\PycharmProjects\NLP\Out"

# Đọc và lưu ảnh
all_records = []
for filename in os.listdir(data_dir):
    path = os.path.join(data_dir, filename)
    if filename.startswith("ETL9G_"):
        if not os.path.isfile(path):
            print(f"File {path} không tồn tại hoặc không phải là file.")
            continue
        print(f"Đang đọc file: {filename}")
        records = read_etl9g(path)
        all_records.extend(records)
save_images_to_folder(all_records, output_folder)

print(f"Đã lưu tổng cộng {len(all_records)} ảnh vào thư mục: {output_folder}")