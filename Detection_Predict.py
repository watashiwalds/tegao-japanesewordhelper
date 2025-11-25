import tensorflow as tf
from PIL import Image, ImageDraw, ImageFont
import numpy as np
import easyocr
import cv2
import json

# =====================================================
# 1) TIỀN XỬ LÝ + PIPELINE
# =====================================================

def preprocess_image(image, img_height=32, img_width=256):
    image = image.convert('L')
    original_w, original_h = image.size
    new_w = int(original_w * (img_height / original_h))
    resized_image = image.resize((new_w, img_height), Image.LANCZOS)

    canvas = Image.new('L', (img_width, img_height), 255)
    canvas.paste(resized_image, (0, 0))

    image_np = np.array(canvas, dtype=np.float32) / 255.0
    image_np = np.expand_dims(image_np, -1)
    return image_np

def decode_predictions(pred, idx_to_char):
    input_len = np.ones(pred.shape[0]) * pred.shape[1]
    results = tf.keras.backend.ctc_decode(pred, input_length=input_len, greedy=True)[0][0]

    texts = []
    for res in results:
        res = res.numpy()
        texts.append(''.join(idx_to_char.get(i, "") for i in res if i > 0))
    return texts

class JapaneseOCRRecognitionPipeline:
    def __init__(self, recognition_model, idx_to_char):
        self.recognition_model = recognition_model
        self.idx_to_char = idx_to_char

    def process_image(self, image, orientation=0):
        if isinstance(image, str):
            image = Image.open(image)

        if orientation == 1:
            image = image.transpose(Image.ROTATE_90)

        # ===============================
        # ĐẢO MÀU NỀN TỐI
        # ===============================
        image = invert_if_dark_background(image)

        img_processed = preprocess_image(image, 32, 256)
        img_batch = np.expand_dims(img_processed, 0)

        pred = self.recognition_model.predict(img_batch, verbose=0)
        return decode_predictions(pred, self.idx_to_char)[0]

# =====================================================
# HÀM INVERT NỀN TỐI
# =====================================================
def invert_if_dark_background(img_pil):
    gray = img_pil.convert('L')
    mean_pixel = np.mean(np.array(gray))
    if mean_pixel < 127:
        img_inverted = Image.fromarray(255 - np.array(gray))
        return img_inverted
    return img_pil

# =====================================================
# 2) LOAD MODEL & CHAR MAP
# =====================================================

inference_model = tf.keras.models.load_model(
    "recognition_inference_model_new.keras", compile=False
)

with open("char_to_idx.json", "r", encoding="utf-8") as f:
    char_to_idx = json.load(f)

idx_to_char = {v: k for k, v in char_to_idx.items()}

pipeline = JapaneseOCRRecognitionPipeline(inference_model, idx_to_char)

# =====================================================
# 3) EASYOCR DETECTOR
# =====================================================

reader = easyocr.Reader(["ja"], gpu=False)

# =====================================================
# 4) HÀM CHIA BOX LỚN THÀNH NHIỀU BOX NHỎ
# =====================================================

def split_long_box(xmin, ymin, xmax, ymax, max_width=256):
    box_width = xmax - xmin
    if box_width <= max_width:
        return [(xmin, ymin, xmax, ymax)]
    segments = []
    num_segments = int(np.ceil(box_width / max_width))
    for i in range(num_segments):
        sx1 = xmin + i * max_width
        sx2 = min(sx1 + max_width, xmax)
        segments.append((sx1, ymin, sx2, ymax))
    return segments

# =====================================================
# 5) RECOGNIZE + TRẢ TEXT + BOX
# =====================================================

def recognize_with_pipeline(image_path):
    img = cv2.imread(image_path)
    draw_img = img.copy()
    results = reader.readtext(img, detail=1, paragraph=False)

    boxes = []
    final_results = []

    for bbox, _, conf in results:
        pts = np.array(bbox)
        x_min = int(np.min(pts[:, 0]))
        y_min = int(np.min(pts[:, 1]))
        x_max = int(np.max(pts[:, 0]))
        y_max = int(np.max(pts[:, 1]))
        boxes.append((y_min, x_min, x_max, y_max, conf))

    boxes = sorted(boxes, key=lambda t: (t[0], t[1]))

    for ymin, xmin, xmax, ymax, conf in boxes:
        split_boxes = split_long_box(xmin, ymin, xmax, ymax, max_width=256)
        full_text = ""

        for (sx1, sy1, sx2, sy2) in split_boxes:
            crop = img[sy1:sy2, sx1:sx2]
            crop_pil = Image.fromarray(cv2.cvtColor(crop, cv2.COLOR_BGR2RGB))

            # Sử dụng pipeline với tự động invert
            small_text = pipeline.process_image(crop_pil, orientation=0)
            full_text += small_text

        final_results.append((full_text, xmin, ymin, xmax, ymax))
        cv2.rectangle(draw_img, (xmin, ymin), (xmax, ymax), (0, 255, 0), 2)

    return final_results, draw_img

# =====================================================
# 6) VẼ TEXT BẰNG PIL (TIẾNG NHẬT KHÔNG LỖI FONT)
# =====================================================

FONT_PATH = "NotoSansJP-Medium.ttf"

def draw_text_with_pil(cv_img, text, x, y):
    img_pil = Image.fromarray(cv2.cvtColor(cv_img, cv2.COLOR_BGR2RGB))
    draw = ImageDraw.Draw(img_pil)
    font = ImageFont.truetype(FONT_PATH, 13)
    draw.text((x, y), text, font=font, fill=(255, 0, 0))
    return cv2.cvtColor(np.array(img_pil), cv2.COLOR_RGB2BGR)

# =====================================================
# 7) CHẠY THỬ
# =====================================================

image_path = "Nhat/vb-2.jpg"
results, draw_img = recognize_with_pipeline(image_path)

for idx, (text, xmin, ymin, xmax, ymax) in enumerate(results):
    print(f"Line {idx+1}: {text}")
    draw_img = draw_text_with_pil(draw_img, text, xmin, ymin - 10)

cv2.imwrite("output_result.jpg", draw_img)
print("\nẢnh đã lưu: output_result.jpg")
