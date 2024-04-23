import os

import aioredis
from fastapi import FastAPI
from config import settings
from controller.v1 import api_router as api_router_v1
from exception_handler import  register_exception_handlers

local_env = os.environ.get("ENV_FOR_DYNACONF", None)

open_api_url = '/openapi.json' if local_env != 'prod' else None
# open_api_url = '/openapi.json'

main_app = FastAPI(
    title='FASTapi-后端',
    description='',
    version='0.0.1',
    openapi_url=open_api_url
)

main_app.include_router(api_router_v1)
register_exception_handlers(main_app)


@main_app.on_event('startup')
async def startup():
    main_app.state.redis = await aioredis.Redis.from_url(f'redis://{settings.redis.host}:{settings.redis.port}/2')
    print("redis:", main_app.state.redis)

@main_app.on_event('shutdown')
async def shutdown():
    await main_app.state.redis.close()
