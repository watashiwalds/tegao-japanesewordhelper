import tensorflow as tf
import keras
import numpy as np
from keras.src.applications.mobilenet import MobileNet
from keras.src.legacy.preprocessing.image import ImageDataGenerator
from matplotlib import pyplot as plt
from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay, classification_report, accuracy_score


data_dir = "/kaggle/input/image-processer"

print("TensorFlow version:", tf.__version__)
print("GPU Available:", tf.config.list_physical_devices('GPU'))

BATCH_SIZE = 256
LR = 0.001
N_EPOCHS = 10
IMG_SIZE = 96
N_CLASSES = 3036

datagen = ImageDataGenerator(
    validation_split=0.3
)

train_generator = datagen.flow_from_directory(
    data_dir,
    subset="training",
    target_size=(IMG_SIZE, IMG_SIZE),
    batch_size=BATCH_SIZE,
    shuffle=True
)

test_generator = datagen.flow_from_directory(
    data_dir,
    subset="validation",
    target_size=(IMG_SIZE, IMG_SIZE),
    batch_size=BATCH_SIZE,
    shuffle= False
)

from keras import layers, models

base_model = MobileNet(
    input_shape=(IMG_SIZE, IMG_SIZE, 3),
    include_top=False,
    weights="imagenet"
)

x = base_model.output
x = layers.GlobalAveragePooling2D()(x)
x = layers.Reshape((1,1,1024))(x)
x = layers.Dropout(0.5)(x)
x = layers.Conv2D(N_CLASSES , (1,1), padding="same")(x)
x = layers.Activation("softmax")(x)
output = layers.Reshape((N_CLASSES,))(x)

model = models.Model(inputs=base_model.input, outputs=output)
model.summary()

model.compile(loss=keras.losses.categorical_crossentropy, optimizer=keras.optimizers.Adam(learning_rate=LR), metrics=['accuracy'])

historys = model.fit(
    train_generator,
    epochs=N_EPOCHS,
    validation_data=test_generator
)

model.save("/kaggle/working/mobilenet.keras")

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

# ===== ĐÁNH GIÁ TRÊN TẬP VALIDATION =====
y_pred = model.predict(test_generator)
y_pred_classes = np.argmax(y_pred, axis=1)
y_true = test_generator.classes

# Độ chính xác
acc = accuracy_score(y_true, y_pred_classes)
print("Validation Accuracy:", acc)

# Classification report
print("\nClassification Report:")
print(classification_report(y_true, y_pred_classes))

# Confusion matrix
cm = confusion_matrix(y_true, y_pred_classes)
disp = ConfusionMatrixDisplay(confusion_matrix=cm,
                              display_labels=list(test_generator.class_indices.keys()))
disp.plot(xticks_rotation='vertical', cmap="Blues")
plt.show()