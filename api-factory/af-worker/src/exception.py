from starlette.exceptions import HTTPException


class RequestParamsException(HTTPException):
    def __init__(self, status_code: int = 500, message: str = "请求参数错误") -> None:
        super().__init__(status_code, detail=message)
    