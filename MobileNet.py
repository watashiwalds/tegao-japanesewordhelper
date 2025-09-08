import os
import math
import tensorflow as tf
import cv2
import keras
import numpy as np
from keras.src.applications.mobilenet import MobileNet
from keras.src.legacy.preprocessing.image import ImageDataGenerator
from matplotlib import pyplot as plt
from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay, classification_report, accuracy_score
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.decomposition import PCA

data_dir = "/kaggle/input/image-etl/Out"

print("TensorFlow version:", tf.__version__)
print("GPU Available:", tf.config.list_physical_devices('GPU'))

def process_image(image_path):
    img = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
    crop_margin = 20
    h, w = img.shape
    img = img[crop_margin: h - crop_margin, crop_margin: w - crop_margin]
    kernel = np.ones((5,5), np.uint8)
    img_erosion = cv2.erode(img, kernel, iterations=0)
    img_equalized = cv2.equalizeHist(img_erosion)
    img_gaussian = cv2.GaussianBlur(img_equalized, (5,5), 0)

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

# ----- load data -----
X = []
y = []

labels = sorted(os.listdir(data_dir))
label_dict = {label: idx for idx, label in enumerate(labels)}

def load_data():
    for label in labels:
        if label.startswith("9") or label.startswith("20"):
            print("Loading:", label)
            folder_path = os.path.join(data_dir, label)
            for file in os.listdir(folder_path):
                image_path = os.path.join(folder_path, file)
                img = process_image(image_path)
                X.append(img)
                y.append(label_dict[label])

load_data()
X = np.array(X)
y = np.array(y)

train_datagen = ImageDataGenerator()
test_datagen = ImageDataGenerator()

BATCH_SIZE  = 256
LR = 0.001
N_EPOCHS = 50
IMAGE_SIZE = 96
N_CLASSES = len(labels)

X = np.expand_dims(X, -1)
X = np.repeat(X, 3, axis=-1)

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=10)
from keras.utils import to_categorical

y_train_co = to_categorical(y_train, num_classes=N_CLASSES)
y_test_co  = to_categorical(y_test, num_classes=N_CLASSES)

train_gen = train_datagen.flow(X_train, y_train_co, batch_size=BATCH_SIZE, shuffle=True)
test_gen  = test_datagen.flow(X_test, y_test_co, batch_size=BATCH_SIZE, shuffle=False)

from keras import layers, models

base_model = MobileNet(
    input_shape=(IMAGE_SIZE, IMAGE_SIZE, 3),
    include_top=False,
    weights='imagenet'
)

x = base_model.output
x = layers.GlobalAveragePooling2D()(x)
x = layers.Reshape((1, 1, 1024))(x)
x = layers.Dropout(0.5)(x)
x = layers.Conv2D(N_CLASSES, (1, 1), padding='same')(x)
x = layers.Activation('softmax')(x)
output = layers.Reshape((N_CLASSES,))(x)

model = models.Model(inputs=base_model.input, outputs=output)
model.summary()

model.compile(loss=keras.losses.categorical_crossentropy, optimizer=keras.optimizers.Adam(learning_rate=LR), metrics=['accuracy'])

historys = model.fit(
    train_gen,
    epochs=N_EPOCHS,
    validation_data=test_gen
)

# --- Vẽ training curves ---
plt.figure(figsize=(12, 5))

plt.subplot(1, 2, 1)
plt.plot(historys.history['accuracy'])
plt.plot(historys.history['val_accuracy'])
plt.title('model accuracy')
plt.ylabel('accuracy')
plt.xlabel('epoch')
plt.legend(['train', 'test'], loc='best')

plt.subplot(1, 2, 2)
plt.plot(historys.history['loss'])
plt.plot(historys.history['val_loss'])
plt.title('model loss')
plt.ylabel('loss')
plt.xlabel('epoch')
plt.legend(['train', 'test'], loc='best')
plt.show()