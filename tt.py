import tensorflow as tf
import numpy as np
from tensorflow.keras.preprocessing import image
import json
from PIL import Image
import matplotlib.pyplot as plt

# Đường dẫn
MODEL_PATH = "/kaggle/working/mobilenet.keras"
CLASS_INDICES_PATH = "class_indices.json"
IMG_SIZE = 96

# Tải mô hình
model = tf.keras.models.load_model(MODEL_PATH)

# Tải danh sách lớp
with open(CLASS_INDICES_PATH, 'r') as f:
    class_indices = json.load(f)
class_names = list(class_indices.keys())


def check_image(image_path):
    img = Image.open(image_path)
    print(f"Định dạng ảnh: {img.mode}, Kích thước: {img.size}")
    if img.mode != 'RGB':
        img = img.convert('RGB')
    img = img.resize((IMG_SIZE, IMG_SIZE))
    return img


def predict_image(image_path):
    # Kiểm tra và hiển thị ảnh
    img = check_image(image_path)
    plt.imshow(img)
    plt.title(f"Ảnh đầu vào: {image_path}")
    plt.show()

    # Tiền xử lý
    img_array = image.img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    img_array = img_array / 255.0

    # Dự đoán
    predictions = model.predict(img_array)
    predicted_class = np.argmax(predictions[0])
    predicted_label = class_names[predicted_class]

    # In kết quả
    print(f"Dự đoán: {predicted_label} (Class ID: {predicted_class})")
    print(f"Xác suất: {predictions[0][predicted_class]:.4f}")

    # In top 5 lớp
    top_5_indices = np.argsort(predictions[0])[-5:][::-1]
    top_5_probs = predictions[0][top_5_indices]
    print("Top 5 lớp:")
    for i, idx in enumerate(top_5_indices):
        print(f"Top {i + 1}: {class_names[idx]} (Xác suất: {top_5_probs[i]:.4f})")

    return predicted_label, predictions[0]


# Ví dụ sử dụng
image_path = ""  # Thay bằng đường dẫn thực tế
predicted_label, probabilities = predict_image(image_path)