from typing import Any, List
import typing
from starlette.applications import Starlette
from starlette.responses import JSONResponse
from starlette.routing import Route
from starlette.requests import Request
from starlette.endpoints import HTTPEndpoint
import uvicorn
import uuid
import pathlib
import contextlib
import orjson
from kazoo.protocol.states import WatchedEvent, EventType, ZnodeStat
from loguru import logger
from pydantic import BaseModel

from config import settings
from dynamic_route import DynamicRoute
from exception import RequestParamsException
from schema.resp import R
from exchange import DsWorkerExchange
from service import code_service, route_table_service, route_path_service
from schema.resp.route import SimpleRouteInfo, RouteInfo
import banner
import app_register
from middlewares import MIDDLEWARES


# *****************************
# **********  全局变量 *********
# *****************************
g_routes = []  # 路由表
database_exchange = DsWorkerExchange()


@contextlib.asynccontextmanager
async def lifespan(app: Starlette):
    """
    在 app 启动和关闭时进行加载或关闭某些资源
    :param app: _description_
    """
    banner.print_banner()  # 打印 banner
    app_register.register_zk_startup(app)       # 注册 Zookeeper
    app_register.register_kafka_startup(app)    # 注册 Kafka
    await database_exchange.init()
    # 设置 Zookeeper children 的监听
    logger.info('Begin to watch zookeepr dir `/af/route`')
    @app.state.zk_client.ChildrenWatch("/af/route")
    def watch_children(children: List[str]):
        logger.info('Find the changes of zookeeper dir `/af/route`.')
        align_routes_with_zookeeper(children)
    
    logger.info('Application startup complete.')
    logger.info(f'Uvicorn running on {settings.app.host}:{settings.app.port}')
    yield
    logger.info('Application prepare to endup.')
    await database_exchange.close()
    app_register.register_kafka_endup(app)
    app_register.register_zk_endup(app)
    logger.info('Application endup complete.')


app = Starlette(routes=g_routes, lifespan=lifespan, middleware=MIDDLEWARES)
app.state.APP_ID = str(uuid.uuid1())  # APP 唯一 ID



async def health_check(request):
    """
    健康检查
    :param request: _description_
    :return: _description_
    """
    return JSONResponse({'status': "UP"})


def align_routes_with_zookeeper(route_znodes: List[str]):
    """
    当监听到 Zookeeper 的 `/af/route` 子节点变化时，调用该函数进行路由对齐
    :param route_znodes: _description_
    """
    local_routes = route_table_service.route_path_list()
    remote_routes = set(map(lambda zp: '/dynamic' + zp.replace('.', '/'), route_znodes))
    logger.info('align local-routes and remote-routes')
    # 清理多余的 routes
    for rp in local_routes.difference(remote_routes):
        route = route_table_service.get_route(rp)
        if route:
            logger.info(f'delete route: {rp}')
            app.routes.remove(route)
            route_table_service.delete_route(rp)
    # 创建少的 routes
    for rp in remote_routes.difference(local_routes):
        znode_path = route_path_service.to_znode_path(rp)
        create_route(znode_path)


async def inspect_routes(request):
    """
    返回当前 app 的基本信息
    :param request: _description_
    :return: _description_
    """
    routes: List[SimpleRouteInfo] = [
        {'rid': route.rid, 'name': route.name, 'path': route.path, 'methods': list(route.methods)}
        for route in request.app.routes if isinstance(route, DynamicRoute)
    ]
    resp = {
        'app_id': request.app.state.APP_ID,
        'routes': routes
    }
    return JSONResponse(R.success(resp).model_dump())



async def execute_code(request):
    """
    接收 code 并调用 Python 解释器来执行
    :param request: _description_
    :raises RequestParamsException: _description_
    :return: _description_
    """
    body = await request.json()
    code = body.get('code')
    if not code or not isinstance(code, str):
        raise RequestParamsException()
    exec(code)
    return JSONResponse(R.success("exec success").model_dump())


async def get_openapi_json(request):
    """
    获取一个路由接口的 OpenAPI JSON
    :param request: _description_
    """
    route_path = request.query_params.get('path')
    if not route_path:
        raise RequestParamsException(500, "路由不存在")
    route = route_table_service.get_route('/dynamic' + route_path)
    body_clz = None
    try:
        body_clz = getattr(route.endpoint, 'Body')
    except AttributeError:
        pass
    schema = {}
    if body_clz:
        schema = body_clz.model_json_schema()
    # 参考 https://spec.openapis.org/oas/v3.1.0
    open_api = {
        'openapi': '3.1.0',
        'info': {'title': f"API 调试界面: {route.name}", 'version': 'v0.0.1', 'description': f"API path: {route_path}"},
        "servers": [
            { 'url': '/api/af-worker/dynamic', 'description': '内部用户访问' },
            { 'url': '/public-api', 'description': '外部用户访问' }
        ],
        'schemes': ['http'],
        'paths': {
            route_path: {"post": {
                "summary": f'{route.name}',
                "description": f'{route.desc}',
                "requestBody": {'content': {"application/json": {
                    'schema': schema
                }}},
                'responses': {
                    "200": {
                        "description": "正常响应",
                        "content": {
                            "application/json": {'example': {}}
                        }
                    }
                }
            }}
        }
    }
    return JSONResponse(open_api)


async def get_route_code_for_sql(request):
    """
    获取 SQL 类型 route 的代码
    """
    body = await request.json()
    route_id, sql, ds_conf = body.get('route_id'), body.get('sql'), body.get('ds_conf')
    handler_code, handler_name = code_service.create_sql_api_handler(route_id, sql, ds_conf)
    resp_body = {
        'handler_name': handler_name,
        'handler_code': handler_code
    }
    return JSONResponse(R.success(resp_body).model_dump())


def create_route(zk_path: str):
    print("zk_path: ", zk_path)
    znode_data, znode_stat = app.state.zk_client.get(zk_path)
    route_version = znode_stat.version
    route_info = orjson.loads(znode_data.decode("utf-8"))
    route_id = route_info.get('rid')
    route_name = route_info.get('name')
    route_description = route_info.get('desc')
    if route_description is None:
            route_description = ""
    route_path = '/dynamic' + route_info.get('path')
    if route_table_service.get_route(route_path) is not None:
        raise RequestParamsException("path 重复")
    route_methods = route_info.get('methods')
    handler_name = route_info.get('handler')
    handler_code = route_info.get('code')
    execution_code = f"""{handler_code}
route = DynamicRoute(route_path, {handler_name}, route_id, handler_code, route_version, route_description, methods=route_methods, name=route_name)
route_table_service.put_route(route_path, route)
app.routes.append(route)
    """
    exec(execution_code)
    logger.info(f'create route: {route_path}')
    # 注册对新创建节点的监听
    @app.state.zk_client.DataWatch(zk_path)
    def watch_route_znode(data: bytes, stat: ZnodeStat, event: WatchedEvent):
        if event:
            route_znode_listener(data, stat, event)
            if event.type == EventType.DELETED:
                return False  # 删除时，需要关闭对该 znode 的 watch
    


async def get_route_info(request):
    """
    查看一个 route 的 code
    :param request: _description_
    """
    body = await request.json()
    path = body.get('path')
    if not path or not isinstance(path, str):
        raise RequestParamsException()
    route = route_table_service.get_route(path)
    if not route:
        return JSONResponse(R.fail("不存在此 route").model_dump)
    resp_body: RouteInfo = {
        'rid': route.rid,
        'name': route.name,
        'path': route.path,
        'methods': list(route.methods),
        'code': route.code
    }
    return JSONResponse(R.success(resp_body).model_dump())



def route_znode_listener(data: bytes, stat: ZnodeStat, event: WatchedEvent):
    """
    监听节点数据
    :param event: _description_
    """
    logger.info(f"listened zk event: {event}")
    match event.type:
        case EventType.DELETED:
            route_znode_deleted_event_handler(data, stat, event)
        case EventType.CHANGED:
            route_znode_changed_event_handler(data, stat, event)
        case _:
            pass


def route_znode_deleted_event_handler(data: bytes, stat: ZnodeStat, event: WatchedEvent):
    """
    处理 route znode 的 DELETE 事件
    :param event: _description_
    """
    znode_path = event.path
    route_path = route_path_service.to_route_path(znode_path)
    route = route_table_service.get_route(route_path)
    if route:
        app.routes.remove(route)  # 不用考虑并发真爽！
        route_table_service.delete_route(route_path)
        del route.endpoint
        logger.info('dynamic-route delete: ' + route_path)


def route_znode_changed_event_handler(znode_data: bytes, znode_stat: ZnodeStat, event: WatchedEvent):
    """
    处理 route znode 的 CHANGED 事件
    :param event: _description_
    """
    znode_path = event.path
    route_path = route_path_service.to_route_path(znode_path)
    route = route_table_service.get_route(route_path)
    if route:
        logger.info(f'update route: {route_path}')
        # znode_data, znode_stat = app.state.zk_client.get(znode_path)
        route_version = znode_stat.version
        route_info = orjson.loads(znode_data.decode("utf-8"))
        route_methods = route_info.get('methods')
        route_methods = {method.upper() for method in route_methods}
        if 'GET' in route_methods:
            route_methods.add('HEAD')
        handler_name = route_info.get('handler')
        handler_code = route_info.get('code')
        route_name = route_info.get('name')
        route_desc = route_info.get('desc')
        if route_desc is None:
            route_desc = ""
        execution_code = f"""{handler_code}
route.endpoint = {handler_name}
route.app = {handler_name}
route.methods = route_methods
route.code = handler_code
route.name = route_name
route.version = route_version
route.desc = route_desc
"""
        exec(execution_code)


app.routes.append(Route("/health", health_check))
app.routes.append(Route("/meta/inspect", inspect_routes))
app.routes.append(Route("/meta/exec-code", execute_code, methods=['POST']))
app.routes.append(Route("/meta/route/code/sql", get_route_code_for_sql, methods=['POST']))
app.routes.append(Route("/meta/route/info", get_route_info))
app.routes.append(Route("/meta/route/openapi", get_openapi_json, methods=['GET']))


if __name__ == '__main__':
    # uvicorn.run(app, host=settings.app.host, port=settings.app.port, log_level='critical' ,access_log=False)
    uvicorn.run(app, host=settings.app.host, port=settings.app.port)