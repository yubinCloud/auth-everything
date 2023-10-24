import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from minio import Minio
import json
from loguru import logger

from config import settings
from controller import api_router
from minio_bucket_policy import BUCKET_POLICY



app = FastAPI(
    title='Simulator',
    description='模拟浏览器运行',
    version='0.1'
)

@app.on_event('startup')
def startup_event_of_minio():
    """
    将 minio_client 挂载到 app 上
    """
    minio_client = Minio(
        settings.minio.url,
        access_key=settings.minio.access_key,
        secret_key=settings.minio.secret_key,
        secure=False
    )
    # 初始化 bucket
    bucket_name = settings.minio.bucket_name
    found = minio_client.bucket_exists(bucket_name)
    if not found:
        minio_client.make_bucket(bucket_name)
        logger.info(f'MinIO 创建 bucket {bucket_name} 成功')
    minio_client.set_bucket_policy(bucket_name, json.dumps(BUCKET_POLICY))
    logger.info(f'MinIO 设置 bucket {bucket_name} 的 policy 为 public')
    app.state.minio_client = minio_client

app.include_router(api_router)


if __name__ == '__main__':
    origins = ['*']
    app.add_middleware(
        CORSMiddleware,
        allow_origins=origins,
        allow_credentials=False,
        allow_methods=["*"],
        allow_headers=["*"],
    )
    uvicorn.run(app, host=settings.app.host, port=settings.app.port)