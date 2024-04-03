from argparse import ArgumentParser
import pandas as pd
import numpy as np
from icecream import ic as log
from utils import get_file_logger
from inmet_extractor import extract_inmet_data
import numpy as np

def main(inmet_folder:str, inmet_result_file:str):

    if not inmet_result_file and not inmet_folder:
        raise FileNotFoundError("Não há dados do INMET para processar")
    if not inmet_result_file and inmet_folder:
        inmet_result_file = 'inmet_output.csv'
        extract_inmet_data(input_folder=inmet_folder, output_file=inmet_result_file)
    df = pd.read_csv(inmet_result_file, on_bad_lines='skip', delimiter=';')
    # 'Hora UTC', 'Data',

    float_columns = [
        'PRECIPITAÇÃO TOTAL, HORÁRIO (mm)',
        'PRESSAO ATMOSFERICA AO NIVEL DA ESTACAO, HORARIA (mB)',
        'PRESSÃO ATMOSFERICA MAX.NA HORA ANT. (AUT) (mB)',
        'PRESSÃO ATMOSFERICA MIN. NA HORA ANT. (AUT) (mB)',
        'RADIACAO GLOBAL (Kj/m²)',
        'TEMPERATURA DO AR - BULBO SECO, HORARIA (°C)',
        'TEMPERATURA DO PONTO DE ORVALHO (°C)',
        'TEMPERATURA MÁXIMA NA HORA ANT. (AUT) (°C)',
        'TEMPERATURA MÍNIMA NA HORA ANT. (AUT) (°C)',
        'TEMPERATURA ORVALHO MAX. NA HORA ANT. (AUT) (°C)',
        'TEMPERATURA ORVALHO MIN. NA HORA ANT. (AUT) (°C)',
        'VENTO, RAJADA MAXIMA (m/s)',
        'VENTO, VELOCIDADE HORARIA (m/s)',
        'LATITUDE',
        'LONGITUDE'
    ]

    df[float_columns] = df[float_columns].apply(lambda x: x.str.replace(',', '.')).astype(np.float64)
    print(df.head())
if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('--debug', action='store_true')
    parser.add_argument('--debug-log-file', type=str)
    parser.add_argument('--model-job-file', type=str, help='Arquivo com um modelo treinado no formato .joblib')
    parser.add_argument('--inmet-folder', type=str, help='Pasta com os arquivos do INMET',
                        default='./dados_inmet_2023')
    parser.add_argument('--inmet-result-file', type=str, help='Nome do arquivo de saída com os dados do INMET',
                        default='inmet_output.csv')

    args = parser.parse_args()
    debug = args.debug
    log_file = args.debug_log_file
    if not debug:
        log.disable()
    if log_file is not None:
        log.enable()
        log.configureOutput(outputFunction=get_file_logger(log_file))
    main(inmet_folder=args.inmet_folder, inmet_result_file=args.inmet_result_file)
