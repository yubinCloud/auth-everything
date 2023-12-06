from starlette.applications import Starlette
from kazoo.client import KazooClient

from config import settings


def register_zk_startup(app: Starlette):
    zk = KazooClient(settings.zookeeper.hosts)
    zk.start()
    app.state.zk_client = zk


def register_zk_endup(app: Starlette):
    app.state.zk_client.close()