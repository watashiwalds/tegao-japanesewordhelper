import cv2
import numpy as np
import os
from matplotlib import pyplot as plt

data_dir = "Out"
# Hàm tiền xử lý ảnh
def preprocess_image(img_path):
    img = cv2.imread(img_path, cv2.IMREAD_GRAYSCALE)
    img_blur = cv2.GaussianBlur(img, (5, 5), 0)
    _, thresh = cv2.threshold(img_blur, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
    kernel = np.ones((3,3), np.uint8)
    thresh = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)
    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    if contours:
        valid_contours = [c for c in contours if 200 < cv2.contourArea(c) < 2000]

        if valid_contours:
            boxes = [cv2.boundingRect(c) for c in valid_contours]
            x_min = min([x for x, y, w, h in boxes])
            y_min = min([y for x, y, w, h in boxes])
            x_max = max([x + w for x, y, w, h in boxes])
            y_max = max([y + h for x, y, w, h in boxes])

            img_crop = img[y_min:y_max, x_min:x_max]
        else:
            img_crop = img

    img_resized = cv2.resize(img_crop, (128, 128))
    img_norm = img_resized.astype(np.float32) / 255.0
    return np.expand_dims(img_norm, axis=-1)

# Thu thập đường dẫn ảnh
Image_paths = []
for subdir, dirs, files in os.walk(data_dir):
    for file in files:
        if file.endswith(".png"):
            Image_paths.append(os.path.join(subdir, file))

X = preprocess_image(Image_paths[100000])
print(X.shape)
print(X[0])
plt.imshow(X.squeeze(), cmap="gray")
plt.show()
