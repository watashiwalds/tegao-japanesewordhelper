from jmdict_etl import parse_jmdict
from kanjidic_etl import parse_kanjidic
from tatoeba_etl import load_tatoeba

def run_etl():
    parse_kanjidic('kanjidic2.xml')
    parse_jmdict('JMdict')
    load_tatoeba('Tatoeba.tsv')
    print('Done')

if __name__ == '__main__':
    run_etl()