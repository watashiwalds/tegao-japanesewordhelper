import cv2
import numpy as np
from keras.src.legacy.preprocessing.image import ImageDataGenerator
from matplotlib import pyplot as plt
from tensorflow.keras.models import load_model


def process_image(image_path):
    img = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
    if img is None:
        raise ValueError(f"Không thể đọc ảnh: {image_path}")

    # Cắt viền
    crop_margin = 20
    h, w = img.shape
    img_cropped = img[crop_margin:h - crop_margin, crop_margin:w - crop_margin]

    # Làm mờ Gaussian
    img_blur = cv2.GaussianBlur(img_cropped, (5, 5), 0)
    img_blur = cv2.equalizeHist(img_blur)
    # Ngưỡng Otsu
    _, thresh = cv2.threshold(img_blur, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)

    # Xử lý hình thái học
    kernel = np.ones((3, 3), np.uint8)

    thresh = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)
    # Thêm Erosion
    thresh_eroded = cv2.erode(img_blur, kernel, iterations=1)

    contours, _ = cv2.findContours(thresh, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)

    if contours:
        valid_contours = [c for c in contours if 200 < cv2.contourArea(c) < 3000]
        if valid_contours:
            boxes = [cv2.boundingRect(c) for c in valid_contours]
            x_min = min([x for x, y, w, h in boxes])
            y_min = min([y for x, y, w, h in boxes])
            x_max = max([x + w for x, y, w, h in boxes])
            y_max = max([y + h for x, y, w, h in boxes])

            expand = 20
            x_min = max(0, x_min - expand)
            y_min = max(0, y_min - expand)
            x_max = min(w, x_max + expand)
            y_max = min(h, y_max + expand)

            img_crop = thresh_eroded[y_min:y_max, x_min:x_max]
        else:
            img_crop = thresh_eroded
    else:
        img_crop = thresh_eroded

    # Thay đổi kích thước ảnh
    img_resized = cv2.resize(img_crop, (96, 96))

    # Chuyển grayscale sang RGB bằng cách lặp lại kênh
    img_rgb = cv2.cvtColor(img_resized, cv2.COLOR_GRAY2RGB)

    # Chuẩn hóa và thêm chiều batch
    img_norm = img_rgb.astype("float32") / 255.0
    img_norm = np.expand_dims(img_norm, axis=0)  # Shape: (1, 96, 96, 3)

    return img_norm

# Sử dụng hàm
img_dir = "Out/12861/image_223_jis_12861_1.png"
img = process_image(img_dir)
print(img.shape)
plt.imshow(img[0])
plt.axis("off")
plt.show()
# Tải mô hình
model = load_model("mobilenet_with_labels.keras", compile=False)

class_indices = model.class_indices
idx_to_class = {v: k for k, v in class_indices.items()}

# Dự đoán
prediction = model.predict(img)[0]

# Lấy top 5 dự đoán
top5_idx = np.argsort(prediction)[-5:][::-1]
top5_prob = prediction[top5_idx]

print("Top 5 lớp dự đoán:")
for i in range(5):
    class_name = idx_to_class[top5_idx[i]]
    print(f"{class_name} ({top5_idx[i]}): {top5_prob[i] * 100:.2f}%")