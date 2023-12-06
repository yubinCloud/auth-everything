from typing import TypedDict, Dict, Any, List
from config import settings
import httpx



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
        self.base_url = 'http://localhost:9300/ds-worker'
        if settings.mode == 'prod':
            self.base_url = f'http://{settings.sidecar.host}:{settings.sidecar.port}/ds-worker/ds-worker'
    
    async def exec_select(self, ds_conf: DatasourceConf, sql: str, slots: Dict[str, SQLSlot]) -> List[Dict[str, Any]]:
        request_body = self._parepare_body(ds_conf, sql, slots)
        async with httpx.AsyncClient(base_url=self.base_url) as client:
            r = await client.post('/exec/select', json=request_body)
        return r.json().get('data')
    
    async def exec_update(self, ds_conf: DatasourceConf, sql: str, slots: Dict[str, SQLSlot]) -> int:
        request_body = self._parepare_body(ds_conf, sql, slots)
        async with httpx.AsyncClient(base_url=self.base_url) as client:
            r = await client.post('/exec/update', json=request_body)
        return r.json().get('data')

    def _parepare_body(self, ds_conf: DatasourceConf, sql: str, slots: Dict[str, SQLSlot]):
        return {
            'dataSourceConf': {
                'driverClass': ds_conf['driver_class'],
                'url': ds_conf['url'],
                'username': ds_conf['username'],
                'password': ds_conf['password']
            },
            'sql': sql,
            'slots': {k: {'type': v['type'], 'value': v['value']} for k, v in slots.items()}
        }

ds_worker_exchange = DsWorkerExchange()