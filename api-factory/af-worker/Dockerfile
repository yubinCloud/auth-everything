FROM python:3.11-bullseye

WORKDIR /app
COPY ./requirements.txt .
RUN pip install --no-cache-dir --upgrade -r requirements.txt -i https://mirrors.aliyun.com/pypi/simple/
ENV MERGE_ENABLED_FOR_DYNACONF=true
COPY ./src/ .
EXPOSE 8989
CMD ["python", "app.py"]