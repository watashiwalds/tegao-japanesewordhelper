import tensorflow as tf

model = tf.keras.models.load_model("mobilenetv5.keras", compile=False)

converter = tf.lite.TFLiteConverter.from_keras_model(model)

tflite_model = converter.convert()
with open("mobilenet_lite.tflite", "wb") as f:
    f.write(tflite_model)



