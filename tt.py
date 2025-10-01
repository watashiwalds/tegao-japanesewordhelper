import os
import random
import cv2
import numpy as np
import tensorflow as tf
from multiprocessing import Pool

print("TensorFlow version:", tf.__version__)
print("GPU Available:", tf.config.list_physical_devices('GPU'))

data_dir = "/kaggle/input/dataetlnew"
output_img_dir = "/kaggle/working/output"
output_label_file = "/kaggle/working/label.txt"
os.makedirs(output_img_dir, exist_ok=True)

labels = sorted(os.listdir(data_dir))
char2idx = {c: i for i, c in enumerate(labels)}
idx2char = {i: c for c, i in char2idx.items()}
print(f"Number of unique characters: {len(labels)}")

char_img_counts = {label: len(os.listdir(os.path.join(data_dir, label))) for label in labels}
print("Image counts per character (top 5):", sorted(char_img_counts.items(), key=lambda x: x[1], reverse=True)[:5])

weights = [1 / char_img_counts[label] for label in labels]

def augment_image(img):
    """Thêm augmentation cơ bản cho ảnh ký tự"""
    # Xoay nhẹ [-5, 5] độ
    angle = random.uniform(-5, 5)
    h, w = img.shape
    M = cv2.getRotationMatrix2D((w // 2, h // 2), angle, 1)
    img = cv2.warpAffine(img, M, (w, h), borderValue=255)

    # Thêm Gaussian noise
    if random.random() < 0.3:
        noise = np.random.normal(0, 15, img.shape).astype(np.uint8)
        img = cv2.add(img, noise)

    return img


def create_sequence(args):
    seq_id, length, data_dir, labels, weights, output_img_dir = args

    seq_label = random.choices(labels, weights=weights, k=length)
    images = []

    for l in seq_label:
        img_path = random.choice(os.listdir(os.path.join(data_dir, l)))
        img = cv2.imread(os.path.join(data_dir, l, img_path), cv2.IMREAD_GRAYSCALE)
        if img is None:
            print(f"Warning: Cannot load {img_path}, skipping")
            return None, None

        # Resize từng ký tự về height=128, width giữ tỉ lệ
        h, w = img.shape
        target_h = 128
        scale = target_h / h
        new_w = int(w * scale)
        img = cv2.resize(img, (new_w, target_h), interpolation=cv2.INTER_CUBIC)

        # Augmentation
        img = augment_image(img)

        images.append(img)

    # Ghép chuỗi ký tự với padding trắng 16px
    padding = np.ones((128, 16), dtype=np.uint8) * 255
    seq_img = images[0]
    for i in range(1, len(images)):
        seq_img = np.hstack([seq_img, padding, images[i]])

    # Chuẩn hóa về [0,1]
    seq_img = seq_img.astype(np.float32) / 255.0

    # Nếu muốn crop/pad về max_width để train dễ hơn
    max_width = 512  # bạn chỉnh tùy dataset
    if seq_img.shape[1] < max_width:
        pad = np.ones((128, max_width - seq_img.shape[1]), dtype=np.float32) * 1.0
        seq_img = np.hstack([seq_img, pad])
    else:
        seq_img = cv2.resize(seq_img, (max_width, 128))

    # Lưu ảnh
    if seq_id < 10:  # lưu sample để debug
        cv2.imwrite(f"{output_img_dir}/sample_{seq_id}.png", (seq_img * 255).astype(np.uint8))
    cv2.imwrite(f"{output_img_dir}/{seq_id}.png", (seq_img * 255).astype(np.uint8))

    return seq_id, "".join(seq_label)

num_samples = 600000
tasks = [(i, random.randint(2, 6)) for i in range(num_samples)]
with open(output_label_file, "w", encoding="utf-8") as f:
    with Pool(processes=4) as pool:
        results = pool.map(create_sequence, tasks)
        for result in results:
            if result is not None:
                seq_id, text = result
                f.write(f"{seq_id}.png\t{text}\n")
                if seq_id < 10:
                    print(f"Sample {seq_id}: {text}")
