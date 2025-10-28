from jmdict_etl import parse_jmdict
from kanjidic_etl import parse_kanjidic
from tatoeba_etl import load_tatoeba_optimized

def run_etl_first_time():
    parse_kanjidic('kanjidic2.xml')
    parse_jmdict('JMdict')
    load_tatoeba_optimized('Tatoeba.tsv')
    print('Done')
def load_tatoeba():
    load_tatoeba_optimized('Tatoeba.tsv')
if __name__ == '__main__':
     run_etl_first_time()
    #load_tatoeba()