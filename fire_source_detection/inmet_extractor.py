"""Extrator de dados da pasta gerada pelo INMET, salva todos em formato csv
Colunas do csv:
Data;Hora UTC;PRECIPITACAO TOTAL, HORARIO (mm);PRESSAO ATMOSFERICA AO NIVEL DA ESTACAO, HORARIA (mB);PRESSAO ATMOSFERICA MAX.NA HORA ANT. (AUT) (mB);PRESSAO ATMOSFERICA MIN. NA HORA ANT. (AUT) (mB);RADIACAO GLOBAL (Kj/m);TEMPERATURA DO AR - BULBO SECO, HORARIA (C);TEMPERATURA DO PONTO DE ORVALHO (C);TEMPERATURA MAXIMA NA HORA ANT. (AUT) (C);TEMPERATURA MINIMA NA HORA ANT. (AUT) (C);TEMPERATURA ORVALHO MAX. NA HORA ANT. (AUT) (C);TEMPERATURA ORVALHO MIN. NA HORA ANT. (AUT) (C);UMIDADE REL. MAX. NA HORA ANT. (AUT) (%);UMIDADE REL. MIN. NA HORA ANT. (AUT) (%);UMIDADE RELATIVA DO AR, HORARIA (%);VENTO, DIRECAO HORARIA (gr) ( (gr));VENTO, RAJADA MAXIMA (m/s);VENTO, VELOCIDADE HORARIA (m/s)
"""
import csv
import os

def extrair_dados_inmet(
        output_file:str = 'dados_inmet_brasil.csv',
        input_folder:str = 'dados_inmet_2023'
    ) -> None:
    """Extrai os dados da pasta gerada pelo INMET e salva em um arquivo csv

    Args:
        output_file (str, optional): path of the result of the aglomeration of the files.
        input_folder (str, optional): name of the folder containing all csvs given by inmet.
    """
    input_folder = 'dados_inmet'
    dados = {}

    for file in os.listdir(input_folder):
        with open(
            input_folder + '/' + file,
            mode='r+',
            encoding='ISO-8859-1',
            newline=''
        ) as arquivo:
            leitor = csv.reader(arquivo, delimiter=';')
            for _ in range(4):
                next(leitor)
            latitude = float(next(leitor)[1].replace(',', '.'))
            longitude = float(next(leitor)[1].replace(',', '.'))
            dados[file] = (latitude, longitude)

    with open(output_file, mode='w', encoding='ISO-8859-1', newline='') as arquivo:
        escritor = csv.writer(arquivo, delimiter=';')
        header:str = "Data;Hora UTC;PRECIPITACAO TOTAL, HORARIO (mm);PRESSAO ATMOSFERICA AO NIVEL DA ESTACAO, HORARIA (mB);PRESSAO ATMOSFERICA MAX.NA HORA ANT. (AUT) (mB);PRESSAO ATMOSFERICA MIN. NA HORA ANT. (AUT) (mB);RADIACAO GLOBAL (Kj/m);TEMPERATURA DO AR - BULBO SECO, HORARIA (C);TEMPERATURA DO PONTO DE ORVALHO (C);TEMPERATURA MAXIMA NA HORA ANT. (AUT) (C);TEMPERATURA MINIMA NA HORA ANT. (AUT) (C);TEMPERATURA ORVALHO MAX. NA HORA ANT. (AUT) (C);TEMPERATURA ORVALHO MIN. NA HORA ANT. (AUT) (C);UMIDADE REL. MAX. NA HORA ANT. (AUT) (%);UMIDADE REL. MIN. NA HORA ANT. (AUT) (%);UMIDADE RELATIVA DO AR, HORARIA (%);VENTO, DIRECAO HORARIA (gr) ( (gr));VENTO, RAJADA MAXIMA (m/s);VENTO, VELOCIDADE HORARIA (m/s)"
        header_columns = header.split(';')
        escritor.writerow( header_columns+ ['LATITUDE', 'LONGITUDE'])
        for file in os.listdir(input_folder):
            with open(
                input_folder + '/' + file, mode='r+',
                encoding='ISO-8859-1',
                newline=''
            ) as arquivo:
                leitor = csv.reader(arquivo, delimiter=';')
                for _ in range(9):
                    next(leitor)
                for linha in leitor:
                    linha = linha[:-1]
                    escritor.writerow(linha+ list(dados[file]))
