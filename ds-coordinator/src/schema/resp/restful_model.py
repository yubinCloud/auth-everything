"""
Restful 风格的统一返回格式

@File: restful_model.py
"""
from pydantic import Field
from pydantic.generics import GenericModel
from typing import Generic, TypeVar, Optional, List

T = TypeVar('T')  # 泛型类型 T


class RestfulModel(GenericModel, Generic[T]):
    """
    RESTful 风格的数据模型，所有 response 统一采用改模型

    Examples
    --------
    >>> from fastapi import FastAPI
    >>> from typing import Dict
    >>> app = FastAPI()
    >>>
    >>> @app.get('/test/', response_model=RestfulModel[Dict])
    >>> def view_func():
    >>>     ...
    """
    code: int = Field(default=0, title='错误码', description='正常状态下返回 0')
    msg: str = Field(default='', title='状态消息', description='给出调用者本次接口运行的状态提示信息')
    data: Optional[T] = Field(default={}, title='响应的数据部分')

    @staticmethod
    def response(data:dict):
        """
        自定义返回数据。
        data中必须要有code、msg和data参数
        """
        return RestfulModel(**data)

    @staticmethod
    def success(data:dict):
        """
        返回成功信息。
        使用时，data中必须携带msg和data参数。code将默认为1
        """
        return RestfulModel(**data)

    @staticmethod
    def error(data:dict):
        """
        返回错误信息。
        使用时，data中必须携带msg和data参数。code将默认为-1
        """
        data["code"] = -1
        return RestfulModel(**data)



class PageInfo(GenericModel, Generic[T]):
    """
    用于分页的 schema
    """
    total: int = Field(title='总的记录个数')
    list: List[T] = Field(title='记录的列表')
