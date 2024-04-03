"""Extrator de dados da pasta gerada pelo INMET, salva todos em formato csv
Colunas do csv:
Data;Hora UTC;PRECIPITACAO TOTAL, HORARIO (mm);PRESSAO ATMOSFERICA AO NIVEL DA ESTACAO, HORARIA (mB);PRESSAO ATMOSFERICA MAX.NA HORA ANT. (AUT) (mB);PRESSAO ATMOSFERICA MIN. NA HORA ANT. (AUT) (mB);RADIACAO GLOBAL (Kj/m);TEMPERATURA DO AR - BULBO SECO, HORARIA (C);TEMPERATURA DO PONTO DE ORVALHO (C);TEMPERATURA MAXIMA NA HORA ANT. (AUT) (C);TEMPERATURA MINIMA NA HORA ANT. (AUT) (C);TEMPERATURA ORVALHO MAX. NA HORA ANT. (AUT) (C);TEMPERATURA ORVALHO MIN. NA HORA ANT. (AUT) (C);UMIDADE REL. MAX. NA HORA ANT. (AUT) (%);UMIDADE REL. MIN. NA HORA ANT. (AUT) (%);UMIDADE RELATIVA DO AR, HORARIA (%);VENTO, DIRECAO HORARIA (gr) ( (gr));VENTO, RAJADA MAXIMA (m/s);VENTO, VELOCIDADE HORARIA (m/s)
"""
import csv
import os
from icecream import ic
def get_header(file_path, new_columns:list[str] = ['LATITUDE', 'LONGITUDE', 'UF'])-> list[str]:
    with open(file_path, 'r', encoding='ISO-8859-1') as f:
        leitor = csv.reader(f, delimiter=';')
        for _ in range(8):
            next(leitor)
        header = next(leitor)
        return header[:-1] + new_columns

def extract_inmet_data(input_folder:str, output_file:str) -> None:
    def clean_null_bytes(file_path):
        """Generator function to clean NUL bytes from a file."""
        if file_path.endswith(('.csv', '.CSV')):
            with open(file_path, 'r', encoding='ISO-8859-1') as f:
                for line in f:
                    yield line
        else:
            raise ValueError('File must be a CSV')
    first_file_path = os.path.join(input_folder, os.listdir(input_folder)[0])

    header = get_header(first_file_path)
    with open(output_file, mode='w', encoding='utf-8', newline='') as arquivo_saida:
        escritor = csv.writer(arquivo_saida, delimiter=';')
        escritor.writerow(header)

        for file_name in os.listdir(input_folder):
            file_path = os.path.join(input_folder, file_name)
            lines = clean_null_bytes(file_path)
            metadata = {}
            for _ in range(8):
                line = next(lines)
                value:str|None = None
                try:
                    value = line.split(':')[1].strip().replace(';', '')
                except IndexError:
                    pass
                metadata[line.split(':')[0]] = value
                # Extract metadata including latitude and longitude
                latitude = metadata.get('LATITUDE', "0.0")
                longitude = metadata.get('LONGITUDE', "0.0")
                uf = metadata.get('UF', "XX")
            next(lines)
            # Process data rows
            for linha in csv.reader(lines, delimiter=';'):
                if linha:
                    escritor.writerow(linha[:-1] + [latitude, longitude, uf])


def main():
    extract_inmet_data(input_folder='./dados_inmet_2023', output_file='inmet_output.csv')
if __name__ == '__main__':
    main()
