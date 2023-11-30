class RequestParamsException(Exception):
    def __init__(self, message: str = "请求参数错误") -> None:
        self.message = message
        super().__init__(message)
    