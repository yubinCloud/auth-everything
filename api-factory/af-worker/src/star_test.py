from starlette.applications import Starlette
from starlette.responses import JSONResponse
import uvicorn
from starlette.routing import Route, request_response


g_routes = []
app = Starlette(routes=g_routes)


async def endpoint1(request):
    return JSONResponse({'hello': 'success'})

route1 = Route('/test', endpoint1)

async def endpoint2(request):
    code = """async def endpoint1(request):
    return JSONResponse({'hello2': 'success'})

print(app.routes)
app.routes[0].endpoint = endpoint1
app.routes[0].app = request_response(endpoint1)
"""
    exec(code)
    return JSONResponse({'data': 'success'})

route2 = Route('/modify', endpoint2)


app.routes.append(route1)
app.routes.append(route2)

uvicorn.run(app)