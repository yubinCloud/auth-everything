FROM python:3.9-bullseye

WORKDIR /app

COPY requirements.txt requirements.txt

COPY site-packages /tmp/site-packages

RUN pip install -r requirements.txt --no-index --find-links=/tmp/site-packages

COPY entrypoint.sh /app/entrypoint.sh

COPY ./src /app/src

RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]