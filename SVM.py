import os
import cv2
import numpy as np
from matplotlib import pyplot as plt
from sklearn.metrics import accuracy_score, confusion_matrix
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.svm import SVC
import seaborn as sns
from sklearn.preprocessing import StandardScaler
from sklearn.decomposition import PCA

data_dir = "/kaggle/input/image-etl/Out"


def preprocess_image(img_path):
    img = cv2.imread(img_path, cv2.IMREAD_GRAYSCALE)
    crop_margin = 20
    h, w = img.shape
    img = img[crop_margin:h - crop_margin, crop_margin:w - crop_margin]
    kernel = np.ones((5, 5), np.uint8)
    img = cv2.erode(img, kernel, iterations=0)
    img = cv2.equalizeHist(img)
    img_blur = cv2.GaussianBlur(img, (5, 5), 0)
    _, thresh = cv2.threshold(img_blur, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
    kernel = np.ones((3, 3), np.uint8)
    thresh = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)

    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

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
            x_max = min(img.shape[1], x_max + expand)
            y_max = min(img.shape[0], y_max + expand)

            img_crop = img_blur[y_min:y_max, x_min:x_max]
        else:
            img_crop = img_blur
    else:
        img_crop = img_blur

    img_resized = cv2.resize(img_crop, (128, 128))
    img_norm = img_resized.astype(np.float32) / 255.0
    return img_norm


X = []
y = []

labels = os.listdir(data_dir)
label_dict = {label: idx for idx, label in enumerate(labels)}


def load_data():
    for label in labels:
        if label.startswith("9") or label.startswith("20"):
            print(label)
            folder_path = os.path.join(data_dir, label)
            for file in os.listdir(folder_path):
                img_path = os.path.join(folder_path, file)
                try:
                    img = preprocess_image(img_path)
                    X.append(img)
                    y.append(label_dict[label])
                except Exception as e:
                    print(e)


load_data()
X = np.array(X)
y = np.array(y)

print("Original shape:", X.shape)
X = X.reshape(X.shape[0], -1)
print("Reshaped shape:", X.shape)

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

scaler = StandardScaler()
X_train = scaler.fit_transform(X_train)
X_test = scaler.transform(X_test)

# Giảm chiều bằng PCA
pca = PCA(n_components=100)
X_train_pca = pca.fit_transform(X_train)
X_test_pca = pca.transform(X_test)

svm_model = SVC(kernel='rbf', random_state=42)
svm_model.fit(X_train, y_train)

y_pred = svm_model.predict(X_test)

accuracy = accuracy_score(y_test, y_pred)
print(f"Test Accuracy: {accuracy * 100:.2f}%")

cm = confusion_matrix(y_test, y_pred)
print("Confusion Matrix:\n", cm)

num_classes = len(np.unique(y))
plt.figure(figsize=(10, 8))
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=labels[:num_classes], yticklabels=labels[:num_classes])
plt.title('Confusion Matrix')
plt.ylabel('True Label')
plt.xlabel('Predicted Label')
plt.show()
import random

num_samples = 10
indices = random.sample(range(len(X_test_pca)), num_samples)

plt.figure(figsize=(15, 6))
for i, idx in enumerate(indices):
    img = X_test[idx].reshape(128, 128)

    true_label = [k for k, v in label_dict.items() if v == y_test[idx]][0]
    pred_label = [k for k, v in label_dict.items() if v == y_pred[idx]][0]

    plt.subplot(2, 5, i + 1)
    plt.imshow(img, cmap="gray")
    plt.title(f"T:{true_label}\nP:{pred_label}", fontsize=10)
    plt.axis("off")

plt.suptitle("Một số mẫu ngẫu nhiên từ tập test", fontsize=14)
plt.tight_layout()
plt.show()