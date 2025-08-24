import os

import cv2
import numpy as np
from matplotlib import pyplot as plt
from numpy.array_api import astype
from sklearn.metrics import accuracy_score, confusion_matrix
from sklearn.model_selection import train_test_split
from sklearn.svm import SVC
import seaborn as sns


data_dir = "Out"

def preprocess_image(img_path):
    img = cv2.imread(img_path,cv2.IMREAD_GRAYSCALE)
    img_blur = cv2.GaussianBlur(img,(5,5),0)
    _, thresh = cv2.threshold(img_blur,0,255,cv2.THRESH_BINARY+cv2.THRESH_OTSU)
    kernel = np.ones((3,3),np.uint8)
    thresh = cv2.morphologyEx(thresh,cv2.MORPH_OPEN,kernel)
    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    if contours:
        valid_contours = [c for c in contours if 200 < cv2.contourArea(c) <2000]
        if valid_contours:
            boxes = [cv2.boundingRect(c) for c in valid_contours]
            x_min = min([x for x, y, w, h in boxes])
            y_min = min([y for x, y, w, h in boxes])
            x_max = max([x + w for x, y, w, h in boxes])
            y_max = max([y + h for x, y, w, h in boxes])
            img_crop = img[y_min:y_max, x_min:x_max]
        else:
            img_crop = img
    else:
        img_crop = img

    img_resized = cv2.resize(img_crop,(128,128))
    img_norm = img_resized,astype(np.float32) / 255.0
    return np.expand_dims(img_norm, axis=-1)

X = []
y = []

labels = os.listdir(data_dir)
label_dict = {label: idx for idx, label in enumerate(labels)}

def load_data():
    for label in labels:
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

print(X.shape)
X = X.reshape(X.shape[0],-1)
print(X.shape)

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

svm_model = SVC(kernel='rbf', random_state=42)
svm_model.fit(X_train, y_train)

y_pred = svm_model.predict(X_test)

accuracy = accuracy_score(y_test, y_pred)
print(f"Test Accuracy: {accuracy * 100:.2f}%")

cm = confusion_matrix(y_test, y_pred)

plt.figure(figsize=(10, 8))
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=labels, yticklabels=labels)
plt.title('Confusion Matrix')
plt.ylabel('True Label')
plt.xlabel('Predicted Label')
plt.show()