import sys
from lxml import etree
from db_config import get_connection
import re
import psycopg2
from psycopg2 import Error

def get_or_create_source(cursor, name, update_date=None, description=None, version=None):
    cursor.execute("""
        INSERT INTO Sources (source_name, update_date, description, version)
        VALUES (%s, %s, %s, %s)
        ON CONFLICT (source_name) DO UPDATE
            SET update_date = COALESCE(EXCLUDED.update_date, Sources.update_date),
                description = COALESCE(EXCLUDED.description, Sources.description),
                version = COALESCE(EXCLUDED.version, Sources.version)
        RETURNING source_id
    """, (name, update_date, description, version))
    return cursor.fetchone()[0]

def upsert_tag(cursor, tag_name, description=None):
    print(f"Attempting to upsert tag: {tag_name}")
    try:
        cursor.execute("""
            INSERT INTO Tags (tag_name, description)
            VALUES (%s, %s)
            ON CONFLICT (tag_name) DO NOTHING
            RETURNING tag_id
        """, (tag_name, description))
        result = cursor.fetchone()
        if result:
            print(f"Inserted tag_id={result[0]} for {tag_name}")
            return result[0]
        cursor.execute("SELECT tag_id FROM Tags WHERE tag_name = %s", (tag_name,))
        result = cursor.fetchone()
        return result[0] if result else None
    except psycopg2.Error as e:
        error_msg = str(e)
        print(f"Error inserting tag {tag_name}: {error_msg} - Skipping tag")
        return None

def upsert_word(cursor, reading, writing, pos, freq, source_id):
    print(f"Attempting insert: reading={reading}, writing={writing}, pos={pos}, freq={freq}")
    try:
        cursor.execute("""
                       INSERT INTO Words (primary_reading, primary_writing, part_of_speech, frequency_rank, source_id)
                       VALUES (%s, %s, %s, %s, %s) ON CONFLICT (primary_reading, primary_writing) DO
                       UPDATE
                           SET part_of_speech = COALESCE(EXCLUDED.part_of_speech, Words.part_of_speech),
                           frequency_rank = COALESCE(EXCLUDED.frequency_rank, Words.frequency_rank)
                           RETURNING word_id
                       """, (reading, writing, pos, freq, source_id))
        result = cursor.fetchone()
        if result:
            print(f"Inserted word_id={result[0]}")
            return result[0]
        return None
    except psycopg2.Error as e:
        error_msg = str(e)
        print(f"Error inserting word {writing}/{reading}: {error_msg}")
        if e.pgcode == '22001':
            column = getattr(e.diag, 'column_name', 'unknown')
            print(f"Likely column causing error: {column}")
        return None

def insert_sense(cursor, word_id, def_en, def_vi, gloss, sense_no):
    if not word_id:
        print(f"Skipping sense insert: invalid word_id {word_id}")
        return None
    def_en = def_en or 'no definition'
    try:
        cursor.execute("""
                       INSERT INTO Senses (word_id, definition_en, definition_vi, gloss, sense_number)
                       VALUES (%s, %s, %s, %s, %s) RETURNING sense_id
                       """, (word_id, def_en, def_vi, gloss, sense_no))
        result = cursor.fetchone()
        if result:
            print(f"Inserted sense_id={result[0]} for word_id={word_id}")
            return result[0]
        return None
    except psycopg2.Error as e:
        print(f"Error inserting sense for word_id {word_id}: {str(e)}")
        return None

def insert_example(cursor, sense_id, jp, en):
    cursor.execute("""
        INSERT INTO Examples (sense_id, sentence_jp, sentence_en)
        VALUES (%s, %s, %s)   
    """, (sense_id, jp, en))

def insert_word_tag(cursor, word_id, tag_id):
    try:
        cursor.execute("""
            INSERT INTO word_tags (word_id, tag_id)
            VALUES (%s, %s)
            ON CONFLICT DO NOTHING
        """, (word_id, tag_id))
        print(f"Inserted word_tag: word_id={word_id}, tag_id={tag_id}")
    except psycopg2.Error as e:
        print(f"Error inserting word_tag: word_id={word_id}, tag_id={tag_id}, error={e}")

def parse_jmdict(xml_path):
    conn = get_connection()
    if not conn:
        print("Cannot proceed: Database connection failed.")
        return
    cur = conn.cursor()

    source_id = get_or_create_source(cur, 'JMDict', update_date=None, description='JMDict parsed', version=None)
    conn.commit()

    context = etree.iterparse(xml_path, events=('end',), tag='entry', recover=True, huge_tree=True)

    word_map = {}
    word_count = 0
    sense_count = 0

    for event, elem in context:
        kebs = [keb.text for keb in elem.findall('.//k_ele/keb') if keb is not None and keb.text]
        rebs = [reb.text for reb in elem.findall('.//r_ele/reb') if reb is not None and reb.text]

        if not kebs:
            kebs = [rebs[0]] if rebs else [None]

        senses = elem.findall('sense')

        pos_set = set()
        field_set = set()
        for s in senses:
            for pos in s.findall('pos'):
                if pos.text:
                    pos_set.add(pos.text.strip())
            for field in s.findall('field'):
                if field.text:
                    field_set.add(field.text.strip())
        pos_str = ','.join(sorted(pos_set)) if pos_set else None
        pri_elems = elem.findall('.//re_pri') + elem.findall('.//ke_pri')
        freq = None
        nf_vals = [int(p.text[2:]) for p in pri_elems if p.text and p.text.startswith('nf') and p.text[2:].isdigit()]
        if nf_vals:
            freq = min(nf_vals)

        primary_writing = kebs[0] if kebs else rebs[0] if rebs else None
        primary_reading = rebs[0] if rebs else primary_writing

        if not primary_writing or not primary_reading:
            print(f"Skipping entry: no valid writing/reading")
            elem.clear()
            while elem.getprevious() is not None:
                del elem.getparent()[0]
            continue

        try:
            word_id = upsert_word(cur, primary_reading, primary_writing, pos_str, freq, source_id)
            if word_id:
                word_map[(primary_reading, primary_writing)] = word_id
                word_count += 1
                # Insert tags for this word
                for tag_name in pos_set | field_set:  # Union of pos and field
                    tag_id = upsert_tag(cur, tag_name, description=f"Tag from {tag_name}")
                    if tag_id:
                        insert_word_tag(cur, word_id, tag_id)
            else:
                print(f"Failed to insert word {primary_writing}/{primary_reading}, skipping senses")
                conn.rollback()
                elem.clear()
                while elem.getprevious() is not None:
                    del elem.getparent()[0]
                continue
        except Exception as e:
            print(f"Unexpected error: {e}")
            conn.rollback()
            elem.clear()
            while elem.getprevious() is not None:
                del elem.getparent()[0]
            continue

        for sense_no, sense in enumerate(senses, 1):
            gloss_en = [g.text.strip() for g in sense.findall('gloss') if
                        g.get('{http://www.w3.org/XML/1998/namespace}lang') in (None, 'eng') and g.text]
            def_en = '; '.join(gloss_en) if gloss_en else None
            def_vi = None
            gloss = None
            sense_id = insert_sense(cur, word_id, def_en, def_vi, gloss, sense_no)
            if sense_id:
                sense_count += 1
        elem.clear()
        while elem.getprevious() is not None:
            del elem.getparent()[0]

        if word_count % 100 == 0:  # Commit mỗi 100 entries
            try:
                conn.commit()
                print(f"Committed: Processed {word_count} words, {sense_count} senses")
            except psycopg2.Error as e:
                print(f"Commit failed at {word_count} words: {e}")
                conn.rollback()

    # Commit lần cuối
    try:
        conn.commit()
        print(f"Final commit: Processed {word_count} words, {sense_count} senses")
    except psycopg2.Error as e:
        print(f"Final commit failed: {e}")
        conn.rollback()

    cur.close()
    conn.close()
    print(f"JMDict load completed. Total: {word_count} words, {sense_count} senses")