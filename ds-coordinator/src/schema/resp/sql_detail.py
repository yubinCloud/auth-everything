from pydantic import BaseModel, Field
from typing import List, Optional

class SQLConnList(BaseModel):
    id: str = Field(title="id", default=None)
    driverClass: str = Field(title="数据库类型", default=None)
    name: str = Field(title="连接名称", default=None)
    username: str = Field(title="用户名", default=None)
    password: str = Field(title="密码", default=None)
    url: str = Field(title="连接uri", default=None)
    remark: str = Field(title="备注", default=None)
    createUser: str = Field(title="创建人", default=None)
    createDept: str = Field(title="创建部门", default=None)
    createTime: str = Field(title="创建时间", default=None, description="样例：2020-08-08T12:20:06.000Z；类型为：str")
    updateUser: str = Field(title="更新人", default=None)
    updateTime: str = Field(title="更新时间", default=None, description="样例：2020-08-08T12:20:06.000Z；类型为：str")
    status: int = Field(title="状态", default=None, description="暂不清楚作用")
    isDelete: int = Field(title="是否删除", default=None, description="猜测：0为未删除，1为已删除")
    loginid: str = Field(title="登录id", default=None,description="null或'admin'")


class SQLConnListDetail(BaseModel):
    line: Optional[list[SQLConnList]] = Field(title="数据行", default=[], alias="records")
    count: Optional[int] = Field(title="数据总数", default=0, alias="total")


class TableDetail(BaseModel):
    count: int = Field(title="数据总数", default=0, description="此字段的值，暂时无效")
    query_set: list = Field(title="表数据", default=[],description="表数据,内部的list为每一行的数据")


class TableData(BaseModel):
    count: int = Field(title="数据总数", default=0)
    table_data: List[dict] = Field(title="表数据", default=[],description="表数据,内部的list为每一行的数据,具体字段个数未知，需根据表字段确定")


class SearchLine(BaseModel):
    data_source: str = Field(title="所属数据源", default=None)
    data_source_id: str = Field(title="所属数据源id", default=None)
    table_name: str = Field(title="所属数据表", default=None)
    field_name: str = Field(title="字段名", default=None)
    field_type: str = Field(title="数据类型", default=None)
    field_comment: str = Field(title="字段描述", default=None, alias="field_remark")
    class Config:
        # 修改返回的字段名
        fields = {
            "field_remark": "field_comment",
        }


class GlobalSearchDetail(BaseModel):
    """搜索"""
    count: int = Field(title="数据总数", default=0)
    search_result: List[SearchLine] = Field(title="搜索结果", default=[],description="搜索结果,内部的list为每一行的数据")

class DataSourceCacheLog(BaseModel):
    """获取缓存日志"""
    status: int = Field(title="状态", default=0,description="0：为任务开始未结束；1：未任务开始任务结束；-1:从未缓存")
    start_time: Optional[str] = Field(title="开始时间", default="",description="执行缓存任务的开始时间")
    end_time: Optional[str] = Field(title="结束时间", default="",description="执行缓存任务的结束时间")
    conn_id: Optional[str] = Field(title="连接id", default="",description="连接id")

class GtGsxx(BaseModel):
    """个体工商信息"""
    # regno，entname
    uniscid: Optional[str] = Field(title="统一社会信用代码", default="")
    entname: Optional[str] = Field(title="企业名称", default="")

class EntGsxx(BaseModel):
    """企业工商信息"""
    # regno，entname
    uniscid: Optional[str] = Field(title="统一社会信用代码", default="")
    entname: Optional[str] = Field(title="企业名称", default="")


class Ryjy(BaseModel):
    """人员就医信息"""
    # 时间范围：开始时间和结束时间，格式：yyyy-MM-dd
    #zjhm: 数据类型如：['str','str'...]
    zjhm: List[str] = Field(title="证件号码", default=[], description="数据类型如：['str','str'...]")
    start_date: str = Field(title="开始时间", default="", description="格式：yyyy-MM-dd")
    end_date: str = Field(title="结束时间", default="")


class Jyxx(BaseModel):
    """就医信息"""
    # 时间范围：开始时间和结束时间，格式：yyyy-MM-dd
    #zjhm: 数据类型如：['str','str'...]
    qhdm: List[str] = Field(title="区划代码", default=[], description="数据类型如：['str','str'...]")
    start_date: str = Field(title="开始时间", default="", description="格式：yyyy-MM-dd")
    end_date: str = Field(title="结束时间", default="")
