import sys
import pandas as pd
import re
from tqdm import tqdm
from janome.tokenizer import Tokenizer
import psycopg2
from io import StringIO
from db_config import get_connection

tokenizer = Tokenizer()

def load_word_map(cursor):
    cursor.execute("SELECT word_id, primary_reading, primary_writing FROM Words")
    rows = cursor.fetchall()

    word_map = {}
    for word_id, reading, writing in rows:
        if writing:
            word_map[writing] = word_id
        if reading and reading != writing:
            word_map[reading] = word_id

    return word_map

def tokenize_japanese(sentence):
    tokens = []
    for token in tokenizer.tokenize(sentence):
        lemma = token.base_form if token.base_form != '*' else token.surface
        if lemma:
            tokens.append(lemma)
    return tokens

def bulk_insert_examples(conn, data):
    if not data:
        return
    buffer = StringIO()
    for row in data:
        cleaned = []
        for value in row:
            if value is None:
                cleaned.append('\\N')
            else:
                s = str(value)
                s = s.replace('\r','\\r').replace('\n','\\n').replace('\t',' ')
                cleaned.append(s)
        buffer.write('\t'.join(cleaned) + '\n')
    buffer.seek(0)
    with conn.cursor() as cur:
        cur.copy_from(buffer, 'examples', columns=('sense_id', 'sentence_jp', 'sentence_en'))
    conn.commit()


def load_tatoeba_optimized(tsv_path, batch_size = 5000):
    conn = get_connection()
    if not conn:
        print('Database connection failed')
        sys.exit(1)
    cur = conn.cursor()

    cur.execute("""
        INSERT INTO Sources(source_name,description)
        VALUES('Tatoeba', 'Optimized Tatoeba sentences')
        ON CONFLICT (source_name) DO UPDATE SET description = excluded.description
        RETURNING source_id
    """)
    src_id = cur.fetchone()[0]
    conn.commit()

    word_map = load_word_map(cur)

    cur.execute("SELECT sense_id, word_id FROM Senses")
    senses = cur.fetchall()
    word_to_senses = {}
    for sense_id, word_id in senses:
        word_to_senses.setdefault(word_id, []).append(sense_id)
    print(f"Loaded {len(word_to_senses)} senses from DB")

    df = pd.read_csv(tsv_path, sep='\t', header=None,names=['id_jp','sentence_jp', 'id_en', 'sentence_en'],
                     on_bad_lines='skip',dtype=str)

    examples_to_insert =[]
    total_inserted = 0

    for _, row in tqdm(df.iterrows(),total=len(df), desc='Processing Tatoeba'):
        jp = str(row['sentence_jp']) if row['sentence_jp'] else ''
        en = str(row['sentence_en']) if row['sentence_en'] else None
        if not re.search(r'[ぁ-んァ-ン一-龥]',jp):
            continue
        tokens = tokenize_japanese(jp)
        matched_word_ids = set(word_map.get(tok) for tok in tokens if tok in word_map)

        for word_id in matched_word_ids:
            if not word_id:
                continue
            sense_ids = word_to_senses.get(word_id)
            if not sense_ids:
                cur.execute("""
                    INSERT INTO senses (word_id, definition_en, gloss,sense_number)
                    VALUES (%s, %s, %s, %s)
                    RETURNING sense_id
                """, (word_id, None, 'example_inserted',1))
                sense_id = cur.fetchone()[0]
                word_to_senses.setdefault(word_id,[]).append(sense_id)
            else:
                sense_id = sense_ids[0]
            examples_to_insert.append((sense_id, jp,en))
            total_inserted += 1

        if len(examples_to_insert) >= batch_size:
            bulk_insert_examples(conn, examples_to_insert)
            examples_to_insert.clear()
    bulk_insert_examples(conn, examples_to_insert)
    cur.close()
    conn.close()