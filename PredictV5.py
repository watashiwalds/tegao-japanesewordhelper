import cv2
import numpy as np
import pandas as pd
import tensorflow as tf
import json
import time
from matplotlib import pyplot as plt

print("TensorFlow version:", tf.__version__)
print("GPU Available:", tf.config.list_physical_devices('GPU'))

IMG_SIZE = 96


def process_image(image_path):
    img = cv2.imread(image_path)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    img = cv2.resize(img, (IMG_SIZE, IMG_SIZE))
    img = np.expand_dims(img, axis=0)
    return img

model = tf.keras.models.load_model("mobilenetv5.keras", compile=False)


with open("class.json", "r", encoding="utf-8") as f:
    class_indices = json.load(f)
idx_to_class = {v: k for k, v in class_indices.items()}

start_time = time.perf_counter()

img_path = "img_11.png"
img = process_image(img_path)
plt.imshow(img[0], cmap="gray")
plt.show()
pred = model.predict(img)[0]
pred_class = np.argmax(pred)
pred_label = idx_to_class[pred_class]

print("Ảnh:", img_path)
print("Dự đoán:", pred_label, " (xác suất:", pred[pred_class], ")")


top5 = np.argsort(pred)[-10:][::-1]
print("\nTop-10 dự đoán:")
for i in top5:
    print(f"{idx_to_class[i]} : {pred[i]:.4f}")
end_time = time.perf_counter()

elapsed_time = end_time - start_time
print(elapsed_time)