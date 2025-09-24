import os
import math
import tensorflow as tf
import cv2
import keras
import numpy as np
import pandas as pd

data_dir = "/kaggle/input/image-etl9/Out"
label = "/kaggle/input/kanjilabel/jis_kanji_unique.csv"

print("TensorFlow version:", tf.__version__)
print("GPU Available:", tf.config.list_physical_devices('GPU'))

def process_image(image_path):
    img = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
    crop_margin = 20
    h, w = img.shape
    img = img[crop_margin: h - crop_margin, crop_margin: w - crop_margin]
    img = cv2.GaussianBlur(img, (9,9), 1)
    kernel = np.ones((5,5), np.uint8)
    img_erosion = cv2.erode(img, kernel, iterations=0)
    img_equalized = cv2.equalizeHist(img_erosion)
    a = 35
    img_contrast = img_equalized.copy()
    img_contrast[img_contrast > a] = 255
    img_gaussian = img_contrast

    _, thresh = cv2.threshold(img_gaussian,0,255, cv2.THRESH_BINARY_INV+cv2.THRESH_OTSU)
    kernel = np.ones((3,3), np.uint8)
    thresh = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)

    contours, _ = cv2.findContours(thresh, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)

    if contours:
        valid_contours = [c for c in contours if 200 < cv2.contourArea(c) < 3000]
        if valid_contours:
            boxes = [cv2.boundingRect(c) for c in valid_contours]
            x_min = min([x for x,y,w,h in boxes])
            y_min = min([y for x,y,w,h in boxes])
            x_max = max([x+w for x,y,w,h in boxes])
            y_max = max([y+h for x,y,w,h in boxes])

            expand = 20
            x_min = max(0, x_min - expand)
            y_min = max(0, y_min - expand)
            x_max = min(w, x_max + expand)
            y_max = min(h, y_max + expand)

            img_crop = img_gaussian[y_min:y_max, x_min:x_max]
        else:
            img_crop = img_gaussian
    else:
        img_crop = img_gaussian

    img_resized = cv2.resize(img_crop, (96, 96))
    img_norm  = img_resized.astype("float32")/255.0
    return img_norm

df = pd.read_csv(label)
label_real = df['Kanji_char']
label_folder = df['JIS_code']

jis_to_kanji = dict(zip(df['JIS_code'], df['Kanji_char']))

labels = sorted(os.listdir(data_dir))
label_dict = {label: idx for idx, label in enumerate(labels)}

output_dir = "/kaggle/working/"

if not os.path.exists(output_dir):
    os.makedirs(output_dir)

for label in labels:
    kanji_label = jis_to_kanji.get(int(label),label)
    label_output_dir = os.path.join(output_dir, kanji_label)
    if not os.path.exists(label_output_dir):
        os.makedirs(label_output_dir)

def save_processed_image(image, label, filename):
    kanji_label = jis_to_kanji.get(int(label), label)
    label_output_dir = os.path.join(output_dir, kanji_label)
    output_path = os.path.join(label_output_dir, filename)
    image_to_save = (image * 255).astype(np.uint8)
    cv2.imwrite(output_path, image_to_save)

def load_data_and_save():
    for label in labels:
        print(f"Processing and saving: {label} ({jis_to_kanji.get(int(label), label)})")
        folder_path = os.path.join(data_dir, label)
        for file in os.listdir(folder_path):
            image_path = os.path.join(folder_path, file)
            img = process_image(image_path)
            X.append(img)
            y.append(label_dict[label])
            save_processed_image(img, label, file)


X = []
y = []

load_data_and_save()

print("Đã lưu tất cả ảnh đã tiền xử lý vào:", output_dir)