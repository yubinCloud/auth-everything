from pydantic import Field
from pydantic import BaseModel
from typing import Generic, TypeVar


T = TypeVar('T')  # 泛型类型 T

CODE_SUCCESS = 0
CODE_FAILTED = -1


class R(BaseModel, Generic[T]):
    """
    RESTful 风格的数据模型，所有 response 统一采用改模型

    Examples
    --------
    >>> from fastapi import FastAPI
    >>> from typing import Dict
    >>> app = FastAPI()
    >>>
    >>> @app.get('/test/', response_model=R[Dict])
    >>> def view_func():
    >>>     ...
    """
    code: int = Field(default=0, title='错误码', description='正常状态下返回 0')
    message: str = Field(default='', title='状态消息', description='给出调用者本次接口运行的状态提示信息')
    data: T | None = Field(default=None, title='响应的数据部分')

    @staticmethod
    def success(data):
        return R(code=CODE_SUCCESS, message='success', data=data)

    @staticmethod
    def fail(message: str):
        return R(code=CODE_FAILTED, message=message, data=None)
