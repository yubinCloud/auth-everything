import sys

import uvicorn
from fastapi.middleware.cors import CORSMiddleware

import app
from config import settings


if __name__ == '__main__':
    # 前端页面url
    origins = [
        "*"
    ]

    # 后台api允许跨域
    app.main_app.add_middleware(
        CORSMiddleware,
        allow_origins=origins,
        allow_credentials=False,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    if sys.platform == "linux":
        uvicorn.run("app:main_app", host=settings.host, port=settings.port)
    else:
        uvicorn.run("app:main_app", host=settings.host, port=settings.port, reload=True)