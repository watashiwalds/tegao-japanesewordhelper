import os
from dotenv import load_dotenv
from typing import Optional
from google import genai
from google.genai import types
from pydantic import BaseModel, Field

load_dotenv()

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
print(GEMINI_API_KEY)
client = genai.Client(api_key=GEMINI_API_KEY)

class ExampleSentence(BaseModel):
    japanese: str = Field(..., description="Câu ví dụ tiếng Nhật")
    vietnamese: str = Field(..., description="Nghĩa tiếng Việt")


class DictionaryInfo(BaseModel):
    reading: str = Field(..., description="Cách đọc Hiragana/Katakana")
    romaji: str = Field(..., description="Phiên âm Romaji")
    jlpt_level: str = Field(..., description="Cấp độ JLPT (N5-N1)")
    examples: list[ExampleSentence] = Field(..., description="Danh sách câu ví dụ")


class BotResponse(BaseModel):
    intent_type: str = Field(...,description="Loại câu trả lời: 'dictionary' (tra từ) hoặc 'consultation' (tư vấn/hỏi đáp)")
    message_content: str = Field(..., description="Nội dung trả lời chính bằng tiếng Việt (markdown)")
    dictionary_data: Optional[DictionaryInfo] = Field(None, description="Chi tiết từ vựng nếu là tra từ")


def QA_Chatbot(text: str):
    system_instruction = """
    Bạn là một trợ lý học tiếng Nhật thông minh (Sensei).

    Nhiệm vụ của bạn là phân loại ý định của người dùng và trả lời:

    TRƯỜNG HỢP 1: Người dùng TRA TỪ VỰNG hoặc NGỮ PHÁP cụ thể.
    - Set 'intent_type' = 'dictionary'.
    - 'message_content': Giải thích nghĩa, cách dùng ngữ pháp chi tiết.
    - 'dictionary_data': Điền đầy đủ cách đọc, JLPT, và ví dụ.

    TRƯỜNG HỢP 2: Người dùng HỎI TỔNG QUÁT (Lộ trình, phương pháp học, văn hóa, dịch câu dài...).
    - Set 'intent_type' = 'consultation'.
    - 'message_content': Trả lời câu hỏi một cách chi tiết, đưa ra lời khuyên, lộ trình học (có thể dùng Markdown để trình bày đẹp).
    - 'dictionary_data': Để null (None).

    Hãy trả lời thân thiện, dễ hiểu cho người Việt.
    """

    try:
        response = client.models.generate_content(
            model="gemini-2.5-flash-lite",
            contents=text,
            config=types.GenerateContentConfig(
                system_instruction=system_instruction,
                response_mime_type="application/json",
                response_schema=BotResponse,
                temperature=0.5
            )
        )
        return response.parsed

    except Exception as e:
        print(f"Error: {e}")
        return None


if __name__ == "__main__":
    print("--- CASE 1: Tra từ ---")
    res1 = QA_Chatbot("Từ 'kibou' nghĩa là gì?")
    if res1:
        print(f"Loại: {res1.intent_type}")
        print(f"Nội dung: {res1.message_content}")
        if res1.dictionary_data:
            print(f"Đọc: {res1.dictionary_data.reading}")

    print("\n--- CASE 2: Hỏi lộ trình ---")
    res2 = QA_Chatbot("Tôi mới bắt đầu học tiếng Nhật thì lộ trình thế nào?")
    if res2:
        print(f"Loại: {res2.intent_type}")
        print(f"Nội dung: {res2.message_content}")
        # Ở đây dictionary_data sẽ là None
        print(f"Data từ điển: {res2.dictionary_data}")