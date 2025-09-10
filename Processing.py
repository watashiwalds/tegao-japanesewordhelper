import pandas as pd
from sklearn.model_selection import train_test_split
from transformers import TFAutoModel, AutoTokenizer
import tensorflow as tf

Data_dir = "simplifyweibo_4_moods.csv"

df = pd.read_csv(Data_dir)
num_labels = df['label'].nunique()
X = df.iloc[:, 1]
y = df.iloc[:, 0]

print(X.head())
print(y.head())
print(num_labels)

train_df, test_df = train_test_split(df, test_size=0.2, stratify=df["label"], random_state=42)
print(train_df.head())
print(test_df.head())

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
X_test  = encode_texts(test_df['review'])

y_train = tf.keras.utils.to_categorical(train_df['label'], num_classes=num_labels)
y_test  = tf.keras.utils.to_categorical(test_df['label'], num_classes=num_labels)

print(X_train)
print(y_train)
print(X_test)
print(y_test)