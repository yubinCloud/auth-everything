from starlette.applications import Starlette
from kazoo.client import KazooClient
from kafka import KafkaProducer
import orjson

from config import settings


def register_zk_startup(app: Starlette):
    zk = KazooClient(settings.zookeeper.hosts)
    zk.start()
    app.state.zk_client = zk


def register_zk_endup(app: Starlette):
    app.state.zk_client.close()


def register_kafka_startup(app: Starlette):
    if (settings.app.enabled_af_access_log):
        kafka_producer = KafkaProducer(
            bootstrap_servers=settings.kafka.servers,
            value_serializer=lambda v: orjson.dumps(v).encode('utf-8')
        )
        app.state.kafka_producer = kafka_producer


def register_kafka_endup(app: Starlette):
    if (settings.app.enabled_af_access_log):
        app.state.kafka_producer.close()
