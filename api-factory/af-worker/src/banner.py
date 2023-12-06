import pathlib


def print_banner():
    # 打印 banner
    banner = pathlib.Path('./banner.txt')
    with banner.open('r', encoding='utf8') as f:
        print(f.read())
