import functools
import json

import aioredis
from dynaconf import Dynaconf
from fastapi import Request, Depends
from config import settings

import config
from schema.resp import RestfulModel


def use_settings() -> Dynaconf:
    return config.settings

def use_redis_client(request: Request) -> aioredis.Redis:
    return request.app.state.redis


def redis_cache(expiration_time: int = 30*60):
    '''能够缓存api的一个方法'''
    def decorator(func):
        @functools.wraps(func)
        async def wrapper(*args, redis: aioredis.Redis = Depends(use_redis_client), **kwargs):
            if settings.debug:
                result = await func(*args, **kwargs)
                return result.dict()
            page = kwargs.get("page",'')
            id = kwargs.get("id",'')
            table_name = kwargs.get("table_name",'')
            kw_ = kwargs.get("key_word",'')
            cache_key = f"{func.__name__}|{page}|{id}|{table_name}|{kw_}"
            cached_result = await redis.get(cache_key)
            if cached_result is not None:
                print("# data from cache")
                await redis.delete(cache_key)
                return json.loads(cached_result.decode('utf8'))
            result = await func(*args, **kwargs)
            await redis.setex(cache_key, expiration_time, str(json.dumps(result.dict())).encode('utf8'))
            print("# date from api")
            return result.dict()
        return wrapper
    return decorator

def role_check():
    def decorator(func):
        @functools.wraps(func)
        async def wrapper(*args, **kwargs):
            if not kwargs.get("user") and not not settings.DEBUG:
                return {"code": -1, "msg": "role校验失败", "data":{}}
            result = await func(*args, **kwargs)
            return result
        return wrapper
    return decorator

def es_check():
    def decorator(func):
        @functools.wraps(func)
        async def wrapper(*args, **kwargs):
            if not kwargs.get("user") and not not settings.DEBUG:
                return {"code": -1, "msg": "es校验失败", "data":{}}
            es_func_list = [
                'get_dataSource_cache_log',
                'search_keyword',
                'update_data_source_info',
                'update_dataSource',
            ]
            if settings.CLOSE_es and func.__name__ in es_func_list:
                return RestfulModel.response({"code": 0, "msg": "es is close.", "data": {}})
            result = await func(*args, **kwargs)
            return result
        return wrapper
    return decorator