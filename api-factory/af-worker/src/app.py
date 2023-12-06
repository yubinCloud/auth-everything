from typing import Any, List
import typing
from starlette.applications import Starlette
from starlette.responses import JSONResponse
from starlette.routing import Route
from starlette.requests import Request
import uvicorn
import uuid
import pathlib
import contextlib
import orjson
from kazoo.protocol.states import WatchedEvent, EventType
from loguru import logger

from config import settings
from dynamic_route import DynamicRoute
from exception import RequestParamsException
from schema.resp import R
from exchange import ds_worker_exchange as database_exchange
from service import code_service, route_table_service, route_path_service
from schema.resp.route import SimpleRouteInfo, RouteInfo
import banner
import app_register


g_routes = []


@contextlib.asynccontextmanager
async def lifespan(app: Starlette):
    banner.print_banner()  # 打印 banner
    app_register.register_zk_startup(app)  # 注册 Zookeeper
    yield
    app_register.register_zk_endup(app)


app = Starlette(routes=g_routes, lifespan=lifespan)
app.state.APP_ID = str(uuid.uuid1())


async def health_check(request):
    return JSONResponse({'status': "UP"})



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


async def add_route(request: Request):
    """
    增加一个 route
    :param request: _description_
    """
    body = await request.json()
    zk_path = '/af/route/' + body.get('znode')
    znode_data, znode_stat = request.app.state.zk_client.get(zk_path, route_znode_listener)  # TODO 增加一个 watch
    route_version = znode_stat.version
    route_info = orjson.loads(znode_data.decode("utf-8"))
    route_id = route_info.get('rid')
    route_name = route_info.get('name')
    route_path = '/dynamic' + route_info.get('path')
    if route_table_service.get_route(route_path) is not None:
        raise RequestParamsException("path 重复")
    route_methods = route_info.get('methods')
    handler_name = route_info.get('handler')
    handler_code = route_info.get('code')
    execution_code = f"""{handler_code}
route = DynamicRoute(route_path, {handler_name}, route_id, handler_code, route_version, methods=route_methods, name=route_name)
route_table_service.put_route(route_path, route)
request.app.routes.append(route)
    """
    exec(execution_code)
    return JSONResponse(R.success('success').model_dump())
    

async def delete_route(request):
    """
    删除一个 route
    :param request: _description_
    """
    body = await request.json()
    path = body.get('path')
    if not path or not isinstance(path, str):
        raise RequestParamsException()
    route = route_table_service.get_route(path)
    if route:
        request.app.routes.remove(route)
        route_table_service.delete_route(path)
    return JSONResponse(R.success("delete success").model_dump())


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


def route_znode_listener(event: WatchedEvent):
    logger.info(f"listener zk event: {event}")
    if event.type == EventType.DELETED:
        route_znode_delete_event_handler(event)


def route_znode_delete_event_handler(event: WatchedEvent):
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
        logger.info('dynamic-route delete: ' + route_path)


def route_znode_changed_event_handler(event: WatchedEvent):
    """
    处理 route znode 的 CHANGED 事件
    :param event: _description_
    """
    pass
    

app.routes.append(Route("/health", health_check))
app.routes.append(Route("/meta/inspect", inspect_routes))
app.routes.append(Route("/meta/exec-code", execute_code, methods=['POST']))
app.routes.append(Route("/meta/route/code/sql", get_route_code_for_sql, methods=['POST']))
app.routes.append(Route("/meta/route/add", add_route, methods=['POST']))
app.routes.append(Route("/meta/route/delete/path", delete_route, methods=['POST']))
app.routes.append(Route("/meta/route/info", get_route_info))


if __name__ == '__main__':
    uvicorn.run(app, host=settings.app.host, port=settings.app.port)
    