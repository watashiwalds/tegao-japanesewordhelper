import os
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report

import tensorflow as tf
from tensorflow.keras import layers, Model, Input
from tensorflow.keras.optimizers import Adam

from transformers import TFAutoModel, AutoTokenizer
from tensorflow.keras import Input, Model, layers

# =====================
# 1. Load dữ liệu CSV
# =====================
DATA_PATH = "/kaggle/input/simplify/simplifyweibo_4_moods.csv"  # đổi sang file của bạn
df = pd.read_csv(DATA_PATH)[['label', 'review']].dropna()
df['label'] = df['label'].astype(int)

num_labels = df['label'].nunique()
print("Số lớp:", num_labels)

# Train/Val/Test split
train_df, temp_df = train_test_split(df, test_size=0.2, stratify=df['label'], random_state=42)
val_df, test_df   = train_test_split(temp_df, test_size=0.5, stratify=temp_df['label'], random_state=42)

# =====================
# 2. Tokenizer
# =====================
PRETRAINED = "bert-base-chinese"
MAX_LEN = 64
NUM_LABELS = 4
tokenizer = AutoTokenizer.from_pretrained(PRETRAINED)

def encode_texts(texts, max_len=MAX_LEN):
    return tokenizer(
        texts.tolist(),
        truncation=True,
        padding="max_length",
        max_length=max_len,
        return_tensors="np"
    )

X_train = encode_texts(train_df['review'])
X_val   = encode_texts(val_df['review'])
X_test  = encode_texts(test_df['review'])

y_train = tf.keras.utils.to_categorical(train_df['label'], num_classes=num_labels)
y_val   = tf.keras.utils.to_categorical(val_df['label'], num_classes=num_labels)
y_test  = tf.keras.utils.to_categorical(test_df['label'], num_classes=num_labels)

# =====================
# 3. Xây dựng mô hình
# =====================
class BertLayer(tf.keras.layers.Layer):
    def __init__(self, model_name, **kwargs):
        super().__init__(**kwargs)
        self.bert = TFAutoModel.from_pretrained(model_name)

    def call(self, inputs):
        input_ids, attention_mask = inputs
        outputs = self.bert(input_ids=input_ids, attention_mask=attention_mask)
        return outputs.last_hidden_state   # (B, L, 768)

# Inputs
input_ids = Input(shape=(MAX_LEN,), dtype=tf.int32, name="input_ids")
attention_mask = Input(shape=(MAX_LEN,), dtype=tf.int32, name="attention_mask")

# BERT embeddings
sequence_output = BertLayer(PRETRAINED)([input_ids, attention_mask])

# BiLSTM
x = layers.Bidirectional(layers.LSTM(128, return_sequences=True))(sequence_output)

# TextCNN
conv_outputs = []
for k in [3, 4, 5]:
    conv = layers.Conv1D(100, k, padding="same", activation="relu")(x)
    pool = layers.GlobalMaxPooling1D()(conv)
    conv_outputs.append(pool)

x = layers.concatenate(conv_outputs, axis=-1)
x = layers.Dropout(0.5)(x)
outputs = layers.Dense(NUM_LABELS, activation="softmax")(x)

# Model
model = Model(inputs=[input_ids, attention_mask], outputs=outputs)
model.compile(optimizer=tf.keras.optimizers.Adam(0.001),
              loss="categorical_crossentropy",
              metrics=["accuracy"])

model.summary()
history = model.fit(
    {"input_ids": X_train["input_ids"], "attention_mask": X_train["attention_mask"]},
    y_train,
    validation_data=(
        {"input_ids": X_val["input_ids"], "attention_mask": X_val["attention_mask"]},
        y_val
    ),
    epochs=15,
    batch_size=32
)

# =====================
# 5. Evaluate
# =====================
y_pred = model.predict({"input_ids": X_test["input_ids"], "attention_mask": X_test["attention_mask"]})
y_pred_labels = np.argmax(y_pred, axis=1)
y_true_labels = np.argmax(y_test, axis=1)

print("\nClassification Report:")
print(classification_report(y_true_labels, y_pred_labels, digits=4))