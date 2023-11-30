from typing import Any, List
import typing
from starlette.applications import Starlette
from starlette.requests import Request
from starlette.responses import JSONResponse
from starlette.routing import Route
import uvicorn
import uuid

from config import settings
from dynamic_route import DynamicRoute
from exception import RequestParamsException
from schema.resp import R
from exchange import ds_worker_exchange as database_exchange
from service import code_service, route_table_service
from schema.resp.route import SimpleRouteInfo, RouteInfo


app = Starlette(debug=True, routes=[])
app.state.APP_ID = str(uuid.uuid1())


async def homepage(request):
    return JSONResponse({'hello': 'world'})



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


async def add_route_for_sql(request):
    """
    增加一个 route
    :param request: _description_
    """
    body = await request.json()
    path = body.get('path')
    if not path:
        raise RequestParamsException()
    path = '/dynamic' + path
    route_id, sql, ds_conf = body.get('route_id'), body.get('sql'), body.get('ds_conf')
    if route_table_service.get_route(route_id) is not None:
        raise RequestParamsException("route id 重复")
    handler_code, handler_name = code_service.create_sql_api_handler(route_id, sql, ds_conf)
    execution_code = f"""{handler_code}
route = DynamicRoute(path, {handler_name}, route_id, handler_code, methods=['POST'], name=route_id)
route_table_service.put_route(route_id, route)
request.app.routes.append(route)
    """
    exec(execution_code)
    resp_boby: RouteInfo = {
        'rid': route_id,
        'name': route_id,
        'path': path,
        'methods': ['POST'],
        'code': handler_code
    }
    print(handler_code)
    return JSONResponse(R.success(resp_boby).model_dump())
    

async def delete_route(request):
    """
    删除一个 route
    :param request: _description_
    """
    body = await request.json()
    rid = body.get('rid')
    if not rid or not isinstance(rid, str):
        raise RequestParamsException()
    route = route_table_service.get_route(rid)
    if route:
        request.app.routes.remove(route)
        route_table_service.delete_route(rid)
    return JSONResponse(R.success("delete success").model_dump())


async def get_route_info(request):
    """
    查看一个 route 的 code
    :param request: _description_
    """
    body = await request.json()
    rid = body.get('rid')
    if not rid or not isinstance(rid, str):
        raise RequestParamsException()
    route = route_table_service.get_route(rid)
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
    

app.routes.append(Route("/meta/inspect", inspect_routes))
app.routes.append(Route("/meta/exec-code", execute_code, methods=['POST']))
app.routes.append(Route("/meta/route/add/sql", add_route_for_sql, methods=['POST']))
app.routes.append(Route("/meta/route/delete/rid", delete_route, methods=['POST']))
app.routes.append(Route("/meta/route/info", get_route_info))


if __name__ == '__main__':
    uvicorn.run(app, host=settings.app.host, port=settings.app.port)