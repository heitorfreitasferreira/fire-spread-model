def get_file_logger(file: str)-> callable:
    def file_logger(text: str):
        with open(file=file, mode='a', encoding='utf-8') as f:
            f.write(text + '\n')
    return file_logger

def main():
    get_file_logger('log.txt')('Hello, World!')
if __name__ == '__main__':
    main()
