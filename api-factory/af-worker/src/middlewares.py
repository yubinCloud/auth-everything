from starlette.middleware import Middleware
from starlette.middleware.cors import CORSMiddleware
from starlette.middleware.base import BaseHTTPMiddleware, RequestResponseEndpoint
from loguru import logger
from starlette.requests import Request
from starlette.responses import Response
from datetime import datetime

from config import settings



class NormalRouteAccessLogMiddleware(BaseHTTPMiddleware):
    """
    记录每个 route 的访问日志
    :param BaseHTTPMiddleware: _description_
    """
    async def dispatch(self, request: Request, call_next: RequestResponseEndpoint) -> Response:
        response: Response = await call_next(request)
        logger.info(f'{request.client.host}:{request.client.port} - "{request.method} {request.url.path} {request.url.scheme}" {response.status_code}')
        return response


class DynamicRouteAccessLogMiddleware(BaseHTTPMiddleware):
    """
    动态生成路由的访问日志，存入 kafka 中
    :param BaseHTTPMiddleware: _description_
    """
    async def dispatch(self, request: Request, call_next: RequestResponseEndpoint) -> Response:
        begin_time = datetime.now().timestamp()
        response: Response = await call_next(request)
        end_time = datetime.now().timestamp()
        if request.url.path.startswith('/dynamic'):
            username = request.headers.get('X-Euser')
            user_type = 1  # 代表外部用户
            if username:
                user_type = 0  # 代表内部用户
            else:
                username = request.headers.get('User')
            reqeust_body = str(await request.body())
            if len(reqeust_body) > 55:
                reqeust_body = reqeust_body[:55]
            response_body = str(response.body)
            if len(response_body) > 55:
                response_body = response_body[:55]
            log_info = {
                'begin_time': begin_time,
                'end_time': end_time,
                'client_host': request.client.host,
                'client_port': request.client.port,
                'caller_type': user_type,
                'caller_username': username,
                'method': request.method,
                'url': request.url,
                'url_host': request.url.hostname,
                'url_port': request.url.port,
                'url_path': request.url.path,
                'req_body': reqeust_body,
                'res_body': response_body,
                'res_status': response.status_code
            }
            request.app.state.kafka_producer.send(settings.kafka.topic, log_info)
        return response


MIDDLEWARES = [
    Middleware(CORSMiddleware, allow_origins=['*'], allow_credentials=False, allow_methods=['*'], allow_headers=['*']),
    Middleware(NormalRouteAccessLogMiddleware),
]

if (settings.app.enabled_af_access_log):
    MIDDLEWARES.append(Middleware(DynamicRouteAccessLogMiddleware))