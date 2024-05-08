import os
from typing import List, Annotated, Union

import aioredis
from fastapi import APIRouter, Depends, Query, Header
from schema.resp import RestfulModel
from schema.resp import sql_detail
from schema.requ import sql_request

from service import sql_service
from dependencies import redis_cache, use_redis_client, role_check, es_check
from config import settings


sql_router = APIRouter(tags=['关系型数据库'])


@sql_router.get('/test_sql_conn/',
                summary='测试关系型数据库连接(mysql:avue;other:ds-worker)',
                response_model=RestfulModel,
                description="以code和msg为准，data中的内容可忽略"
                )
async def test_sql_conn(
    form: sql_request.create_sql_conn = Depends(sql_request.create_sql_conn),
    header: sql_request.custom_header = Depends(sql_request.custom_header)
):
    result = await sql_service.test_sql_conn(form)
    result.update(data="")
    return RestfulModel.response(result)


@sql_router.post('/create_sql_conn/',
                 summary='添加关系型数据库连接(avue)',
                 response_model=RestfulModel,
                 description="以code和msg为准，data中的内容可忽略"
                 )
@role_check()
async def create_sql_conn(
    header: sql_request.custom_header = Depends(sql_request.custom_header),
    form: sql_request.create_sql_conn = Depends(sql_request.create_sql_conn),
):
    result= await sql_service.create_sql_conn(form, header)
    return RestfulModel.response(result)



@sql_router.get('/get_sql_conn_list/',
                summary='获取"数据源"列表(avue)',
                response_model=RestfulModel[sql_detail.SQLConnListDetail]
                )
@role_check()
async def get_sql_conn_list(
    redis: aioredis.Redis = Depends(use_redis_client),
    header: sql_request.custom_header = Depends(sql_request.custom_header),
    page: sql_request.page_query = Depends(sql_request.page_query),
):
    result = await sql_service.get_sql_conn_list(page, header)
    return RestfulModel.response(result)



@sql_router.get('/get_sql_conn_detail/',
                summary='获取连接详情(avue)',
                response_model=RestfulModel[sql_detail.SQLConnList])
@role_check()
@redis_cache()
async def get_sql_conn_detail(
    redis: aioredis.Redis = Depends(use_redis_client),
    header: sql_request.custom_header = Depends(sql_request.custom_header),
    id: str = Query(..., title='数据库连接id'),
):
    result = await sql_service.get_sql_conn_detail(id, header)
    return RestfulModel.response(result)


@sql_router.post('/update_sql_conn/',
                 summary='更新关系型数据库连接(avue)',
                 response_model=RestfulModel,
                 description="以code和msg为准，data中的内容可忽略"
                 )
@role_check()
async def update_sql_conn(
    header: sql_request.custom_header = Depends(sql_request.custom_header),
    form: sql_request.create_sql_conn() = Depends(sql_request.create_sql_conn),
    id: str = Query(..., title='数据库连接id'),
):
    result = await sql_service.update_sql_conn(id, form, header)
    return RestfulModel.response(result)


@sql_router.get('/delete_sql_conn/',
                summary='删除关系型数据库连接(avue)',
                response_model=RestfulModel,
                description="以code和msg为准，data中的内容可忽略"
                )
@role_check()
async def delete_sql_conn(
    header: sql_request.custom_header = Depends(sql_request.custom_header),
    id: str = Query(..., title='数据库连接id'),
):
    result = await sql_service.delete_sql_conn(id, header)
    return RestfulModel.response(result)


@sql_router.get('/get_sql_table_list/',
                summary='获取"数据库表"列表(返回表名array)(java)',
                response_model=RestfulModel[List[str]]
                )
@role_check()
@redis_cache()
async def get_sql_table_list(
    redis: aioredis.Redis = Depends(use_redis_client),
    header: sql_request.custom_header = Depends(sql_request.custom_header),
    id: str = Query(..., title='数据库连接id'),
):
    result,error = await sql_service.get_sql_table_list(id, header)
    return RestfulModel.response(sql_service.make_response(result, msg=error))

@sql_router.get(
    '/get_sql_table_detail/',
    summary='获取"数据表"的"字段"(java)',
    response_model=RestfulModel[List[str]]
)
@role_check()
async def get_sql_table_detail(
    redis: aioredis.Redis = Depends(use_redis_client),
    header: sql_request.custom_header = Depends(sql_request.custom_header),
    id: str = Query(..., title='数据库连接id'),
    table_name: str = Query(..., title='数据库表名'),
):
    result, msg = await sql_service.get_sql_table_detail(id, table_name, headers=header)
    return RestfulModel.response(sql_service.make_response(result, msg))


@sql_router.get(
    '/get_sql_table_data/',
    summary='获取"数据表"的"数据"(java)',
    response_model=RestfulModel[sql_detail.TableDetail]
)
@role_check()
@redis_cache()
async def get_sql_table_data4(
    redis: aioredis.Redis = Depends(use_redis_client),
    header: sql_request.custom_header = Depends(sql_request.custom_header),
    id: str = Query(..., title='数据库连接id'),
    table_name: str = Query(..., title='数据库表名'),
    page: sql_request.page_query = Depends(sql_request.page_query),
):
    result = await sql_service.get_sql_table_data(id, header, table_name, page)
    # return RestfulModel.response({"code":0, "msg":"success", "data":result})
    return RestfulModel.response(result)


@sql_router.get(
    '/search_keyword/',
    summary="搜索关键字(java)",
    response_model=RestfulModel[sql_detail.GlobalSearchDetail]
)
@role_check()
@es_check()
@redis_cache()
async def search_keyword(
    redis: aioredis.Redis = Depends(use_redis_client),
    header: sql_request.custom_header = Depends(sql_request.custom_header),
    key_word: str = Query(..., title='关键字'),
    page: sql_request.page_query = Depends(sql_request.page_query),
):
    # 搜索关键字，从所有表的字段中搜索符合的关键字，返回连接信息
    result = await sql_service.search_keyword(key_word, page, header)
    return RestfulModel.response(result)

# 一个后台任务，用于定时更新数据库连接的状态
from fastapi import BackgroundTasks
@sql_router.get(
    '/update_data_source_info/',
    summary="更新'数据源'的信息(后台任务)",
    response_model=RestfulModel,
    description="需要在获取数据源的数组之后，找出新添加的数据源，然后调用该接口触发服务端后台任务"
)
@role_check()
@es_check()
async def update_data_source_info(
    background_tasks: BackgroundTasks,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
    conn_id: List[str] = Query(..., title='数据库连接id'),
):
    background_tasks.add_task(sql_service.background_get_data_source_info, conn_id, header)
    return RestfulModel.response({"code":0, "msg":"success", "data":None})

@sql_router.get(
    '/update_dataSource/',
    summary="更新'数据源'的信息（后台任务，接收一个任务id作为参数）",
    response_model=RestfulModel,
    description="发送数据源id，调用该接口触发服务端后台任务"
)
@role_check()
@es_check()
async def update_dataSource(
    background_tasks: BackgroundTasks,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
    conn_id: str = Query(..., title='数据库连接id',description="单次只允许1个"),
):
    background_tasks.add_task(sql_service.background_get_data_source_info, [conn_id], header)
    return RestfulModel.response({"code":0, "msg":"success", "data":None})

@sql_router.get(
    '/get_dataSource_cache_log/',
    summary="获取'数据源'的缓存日志",
    response_model=RestfulModel[sql_detail.DataSourceCacheLog]
)
@role_check()
@es_check()
async def get_dataSource_cache_log(
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await sql_service.get_dataSource_cache_log(header)
    return RestfulModel.response(result)


@sql_router.get('/health')
async def health_check():
    '''健康检查'''
    return {
        "status": "UP"
    }
