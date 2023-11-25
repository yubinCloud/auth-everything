from typing import Any
from starlette.applications import Starlette
from starlette.requests import Request
from starlette.responses import JSONResponse
from starlette.routing import Route
import uvicorn
import uuid

from config import settings
from dynamic_route import AfRoute
from exception import RequestParamsException
from schema.resp import R



class AfHandler:
    def __init__(self) -> None:
        pass
    
    def __call__(self, request: Request) -> JSONResponse:
        pass


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
    routes = [
        {'rid': route.rid, 'name': route.name, 'path': route.path, 'methods': list(route.methods)}
        for route in request.app.routes if isinstance(route, AfRoute)
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



async def delete_route(request):
    """
    删除一个 route
    :param request: _description_
    """
    body = await request.json()
    rid = body.get('rid')
    if not rid or not isinstance(rid, str):
        raise RequestParamsException()
    request.app.routes = list(filter(lambda r: isinstance(r, AfRoute) and r.rid == rid), request.app.routes)
    return JSONResponse(R.success("delete success").model_dump())
    
    

app.routes.append(AfRoute("/homepage", homepage, 'homepage'))
app.routes.append(Route("/meta/inspect", inspect_routes))
app.routes.append(Route("/meta/exec-code", execute_code, methods=['POST']))



if __name__ == '__main__':
    uvicorn.run(app, host=settings.app.host, port=settings.app.port)