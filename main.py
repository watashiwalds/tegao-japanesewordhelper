from jmdict_etl import parse_jmdict
from kanjidic_etl import parse_kanjidic
from tatoeba_etl import load_tatoeba

def run_etl_first_time():
    parse_kanjidic('kanjidic2.xml')
    parse_jmdict('JMdict')
    print('Done')

if __name__ == '__main__':
    run_etl_first_time()