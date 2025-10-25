import sys
from lxml import etree
from db_config import get_connection

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

def upsert_kanji(cursor, character, stroke, radical, on_reading, kun_reading, meaning, grade, jlpt, freq):
    # Truncate fields to avoid 'value too long' error
    max_length = 100
    on_reading = on_reading[:max_length] if on_reading else None
    kun_reading = kun_reading[:max_length] if kun_reading else None
    meaning = meaning[:max_length] if meaning else None

    try:
        cursor.execute("""
            INSERT INTO Kanji (character, stroke_count, radical, on_reading, kun_reading, meaning, grade_level, jlpt_level, frequency)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (character) DO UPDATE
                SET stroke_count = COALESCE(EXCLUDED.stroke_count, Kanji.stroke_count),
                    radical = COALESCE(EXCLUDED.radical, Kanji.radical),
                    on_reading = COALESCE(EXCLUDED.on_reading, Kanji.on_reading),
                    kun_reading = COALESCE(EXCLUDED.kun_reading, Kanji.kun_reading),
                    meaning = COALESCE(EXCLUDED.meaning, Kanji.meaning),
                    grade_level = COALESCE(EXCLUDED.grade_level, Kanji.grade_level),
                    jlpt_level = COALESCE(EXCLUDED.jlpt_level, Kanji.jlpt_level),
                    frequency = COALESCE(EXCLUDED.frequency, Kanji.frequency)
            RETURNING character
        """, (character, stroke, radical, on_reading, kun_reading, meaning, grade, jlpt, freq))
        return cursor.fetchone()[0] if cursor.rowcount else character
    except Exception as e:
        print(f"Error inserting kanji {character}: {e}")
        raise  # Re-raise to debug

def parse_kanjidic(xml_path):
    conn = get_connection()
    cur = conn.cursor()
    src_id = get_or_create_source(cur, 'Kanjidic2', update_date=None, description='Kanjidic2 parsed', version=None)
    conn.commit()

    context = etree.iterparse(xml_path, events=('end',), tag='character', recover=True, huge_tree=True)
    count = 0
    for event, elem in context:
        try:
            literal = elem.findtext('literal')
            if not literal:
                continue

            stroke = None
            sc = elem.find('.//misc/stroke_count')
            if sc is not None and sc.text and sc.text.isdigit():
                stroke = int(sc.text)

            rad_elem = elem.find('.//radical/rad_value[@rad_type="classical"]')
            radical = rad_elem.text if rad_elem is not None else None

            on_list = []
            kun_list = []
            for r in elem.findall('.//reading_meaning/rmgroup/reading'):
                r_type = r.get('r_type')
                if r_type == 'ja_on':
                    if r.text:
                        on_list.append(r.text.strip())
                elif r_type == 'ja_kun':
                    if r.text:
                        kun_list.append(r.text.strip())

            on_reading = ' '.join(on_list) if on_list else None
            kun_reading = ' '.join(kun_list) if kun_list else None

            meanings = []
            for m in elem.findall('.//reading_meaning/rmgroup/meaning'):
                if not m.get('m_lang') or m.get('m_lang') == 'en':
                    if m.text:
                        meanings.append(m.text.strip())
            meaning_text = '; '.join(meanings) if meanings else None

            grade = None
            jlpt = None
            freq = None
            g = elem.find('.//misc/grade')
            if g is not None and g.text and g.text.isdigit():
                grade = int(g.text)
            j = elem.find('.//misc/jlpt')
            if j is not None and j.text and j.text.isdigit():
                jlpt = int(j.text)
            f = elem.find('.//misc/freq')
            if f is not None and f.text and f.text.isdigit():
                freq = int(f.text)

            upsert_kanji(cur, literal, stroke, radical, on_reading, kun_reading, meaning_text, grade, jlpt, freq)
            count += 1
            if count % 1000 == 0:
                conn.commit()
                print(f"Processed {count} kanji...")

        except Exception as e:
            print(f"Error processing {literal}: {e}")
            conn.rollback()
        finally:
            elem.clear()
            while elem.getprevious() is not None:
                del elem.getparent()[0]

    conn.commit()
    cur.close()
    conn.close()
    print(f"Kanjidic2 load completed. Processed {count} kanji.")