from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse

from exception import BusinessException
from schema.resp import RestfulModel
import error_code


def register_exception_handlers(app: FastAPI):
    """
    为 app 注册异常处理器
    :param app:
    :return:
    """
    @app.exception_handler(BusinessException)
    async def business_exec_handler(request: Request, exc: BusinessException):
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content=RestfulModel(code=error_code.BUSINESS_EXEC,
                                 message='业务异常',
                                 data=str(exc)).dict()
        )

