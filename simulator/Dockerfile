FROM python:3.10

EXPOSE 9660
WORKDIR /app
COPY ./requirements.txt .
RUN pip install --no-cache-dir --upgrade -r requirements.txt -i https://mirrors.aliyun.com/pypi/simple/
RUN playwright install chromium --with-deps
COPY ./simulator/ .
CMD ["python", "main.py"]