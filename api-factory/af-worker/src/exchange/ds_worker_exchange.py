from typing import TypedDict, Dict, Any, List, Optional
from pydantic import BaseModel
import aiohttp
import ujson
import orjson

from config import settings
from exception import RequestParamsException



class DatasourceConf(TypedDict):
    driver_class: str
    url: str
    username: str
    password: str


class SQLSlot(TypedDict):
    type: str
    value: Any


class DsWorkerExchange:
    
    def __init__(self) -> None:
        self.base_addr = 'http://localhost:9300'
        self.base_path = '/ds-worker'
        if settings.mode == 'prod':
            self.base_addr = f'http://{settings.sidecar.host}:{settings.sidecar.port}'
            self.base_path = '/ds-worker/ds-worker'
        self.session: aiohttp.ClientSession = None
    
    async def init(self) -> None:
        self.session = aiohttp.ClientSession(self.base_addr, json_serialize=ujson.dumps)
    
    async def close(self):
        await self.session.close()
    
    async def exec_select(self, ds_conf: DatasourceConf, sql: str, slots: Optional[BaseModel], *, timeout=5) -> List[Dict[str, Any]]:
        """
        调用 ds-worker 进行数据库查询
        :param ds_conf: _description_
        :param sql: _description_
        :param slots: _description_
        :param timeout: 连接超时时间，默认 5s
        :return: _description_
        """
        request_body = self._parepare_body(ds_conf, sql, slots)
        result = []
        async with self.session.post(self.base_path + "/exec/select", json=request_body, timeout=timeout) as response:
            if (response.status == 200):
                resp_data = await response.json(loads=orjson.loads)
                result = resp_data['data']
        return result
    
    async def exec_update(self, ds_conf: DatasourceConf, sql: str, slots: Optional[BaseModel], *, timeout=5) -> int:
        request_body = self._parepare_body(ds_conf, sql, slots)
        result = []
        async with self.session.post(self.base_path + "/exec/update", json=request_body, timeout=timeout) as response:
            if (response.status == 200):
                resp_data = await response.json(loads=orjson.loads)
                result = resp_data['data']
        return result

    def _parepare_body(self, ds_conf: DatasourceConf, sql: str, slots: Optional[BaseModel]):
        slots = self._convert_slots(slots)
        return {
            'dataSourceConf': {
                'driverClass': ds_conf['driver_class'],
                'url': ds_conf['url'],
                'username': ds_conf['username'],
                'password': ds_conf['password']
            },
            'sql': sql,
            'slots': slots
        }
    
    def _convert_slots(self, slots: Optional[BaseModel]) -> dict:
        if not slots:
            return {}
        result = {}
        for field_name, field_info in type(slots).model_fields.items():
            anno = field_info.annotation  # 类型
            # 尝试获取 alias
            if getattr(field_info, 'alias'):
                field_name = getattr(field_info, 'alias')
            field_value = getattr(slots, field_name)
            match str(anno):
                case "<class 'str'>" | "<class 'datetime.datetime'>":
                    result[field_name] = {'type': 'STRING', 'value': str(field_value)}
                case "<class 'float'>":
                    result[field_name] = {'type': 'FLOAT', 'value': float(field_value)}
                case "<class 'int'>":
                    result[field_name] = {'type': 'INT', 'value': int(field_value)}
                case _:
                    raise RequestParamsException(500, "slot 类型无法识别")
        return result
