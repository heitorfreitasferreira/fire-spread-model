"""Plot the evolution of the fitness of the model
"""

import os
import argparse
import pandas as pd
import matplotlib.pyplot as plt

def remove_comments_from_csv(csv_path:str):
    """Remove comments from the csv file

    Args:
        csv_path (str): name of the csv file containing the evolution of the fitness

    Returns:
        str: name of the csv file without comments
    """
    removable_file_path:str = 'tmp.csv'
    comment_identifier:str = '#'
    encoding:str = 'utf-8'
    with open(csv_path, 'r', encoding=encoding) as f:
        lines = f.readlines()

        with open(removable_file_path, 'w', encoding=encoding) as new_file:
            for line in lines:
                if not line.startswith(comment_identifier):
                    new_file.write(line)
        return removable_file_path

def main(csv_path:str, show_plot:bool, image_file_name:str):
    """Plot the evolution of the fitness of the model

    Args:
        csv_path (str): name of the csv file containing the evolution of the fitness
    """
    data = pd.read_csv(csv_path)

    plt.plot(data['Best Fitness'], label='Best Fitness')
    plt.plot(data['Average Fitness'], label='Average Fitness')
    plt.plot(data['Worst Fitness'], label='Worst Fitness')
    plt.plot(data['Standard Deviation'], label='Standard Deviation')
    plt.grid()
    plt.xlabel('Generation')
    plt.ylabel('Fitness')
    plt.title('Fitness Evolution')
    plt.legend()
    plt.savefig(image_file_name)

    if show_plot:
        plt.show()

if __name__ == '__main__':
    argparser = argparse.ArgumentParser()
    argparser.add_argument('--csv-path',
                        type=str, required=True,
                        help='Path to the csv file containing the evolution of the fitness'
                        )
    argparser.add_argument('--show-plot',
                        type=bool, default=False,
                        help='Show the plot'
                        )
    args = argparser.parse_args()

    image_path = 'plots/' + args.csv_path.split('.')[0] + '.png'
    TMP_FILE_PATH = remove_comments_from_csv(args.csv_path)
    main(TMP_FILE_PATH, args.show_plot, image_path)
    os.remove(TMP_FILE_PATH)
