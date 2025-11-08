import tensorflow as tf

model = tf.keras.models.load_model("mobilenetv5.keras", compile=False)

converter = tf.lite.TFLiteConverter.from_keras_model(model)

converter.optimizations = [tf.lite.Optimize.DEFAULT]
converter.target_spec.supported_types = [tf.float16]
tflite_model = converter.convert()
with open("model_float16.tflite", "wb") as f:
    f.write(tflite_model)



