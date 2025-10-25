from dotenv import load_dotenv
import os
import psycopg2
from psycopg2 import OperationalError


load_dotenv()

def get_connection():
    try:
        conn = psycopg2.connect(
            database=os.getenv("DB_NAME"),
            user=os.getenv("DB_USER"),
            password=os.getenv("DB_PASSWORD"),
            host=os.getenv("DB_HOST"),
            port=os.getenv("DB_PORT")
        )
        return conn
    except OperationalError as e:
        print("Database connection failed. Check your connection")
        print(e)
        return None

def check_connection():
    conn = get_connection()
    if not conn:
        print("Database connection failed. Check your connection")
        return False
    try:
        cur = conn.cursor()
        cur.execute("SELECT version()")
        version = cur.fetchone()
        print(f"✅ Kết nối thành công! PostgreSQL version: {version[0]}")
        return True
    except OperationalError as e:
        print("Failed to connect to PostgreSQL database: ", e)
        return False
    finally:
        if conn:
            cur.close()
            conn.close()

