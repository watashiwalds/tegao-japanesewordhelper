import cv2
import numpy as np
import pandas as pd
import tensorflow as tf
import json

from matplotlib import pyplot as plt

IMG_SIZE = 96

def process_image(image_path):
    img = cv2.imread(image_path)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    img = cv2.resize(img, (IMG_SIZE, IMG_SIZE))
    img = np.expand_dims(img, axis=0)
    return img

model = tf.keras.models.load_model("v3.keras", compile=False)


with open("class_indices.json", "r", encoding="utf-8") as f:
    class_indices = json.load(f)
idx_to_class = {v: k for k, v in class_indices.items()}

img_path = "img_3.png"
img = process_image(img_path)
plt.imshow(img[0], cmap="gray")
plt.show()
pred = model.predict(img)[0]
pred_class = np.argmax(pred)
pred_label = idx_to_class[pred_class]

print("Ảnh:", img_path)
print("Dự đoán:", pred_label, " (xác suất:", pred[pred_class], ")")

jis_dir = "jis_kanji_unique.csv"
df = pd.read_csv(jis_dir)


top5 = np.argsort(pred)[-5:][::-1]
print("\nTop-5 dự đoán:")
for i in top5:
    jis_code = int(idx_to_class[i])
    kanji_char = df.loc[df["JIS_code"] == jis_code, "Kanji_char"].values[0]
    print(f"{kanji_char} (JIS {jis_code}): {pred[i]:.4f}")
