from fastapi import FastAPI
import nacos

app = FastAPI()

SERVER_ADDRESSES = "localhost:8848"
NAMESPACE = "public"

nacos_client = nacos.NacosClient(SERVER_ADDRESSES, namespace=NAMESPACE)

nacos_client.add_naming_instance('bennett', '127.0.0.1', '8000', heartbeat_interval=True)


@app.get('/health')
def health_check_handler():
    return {'status': "OK"}
