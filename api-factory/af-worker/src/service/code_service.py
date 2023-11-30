from typing import Tuple

from dynamic_route import RouteId
from exchange.ds_worker_exchange import DatasourceConf


def create_sql_api_handler(
        route_id: RouteId,
        sql: str,
        ds_conf: DatasourceConf
) -> Tuple[str, str]:
    handler_name = f'handler_{route_id}'
    code = f"""async def {handler_name}(request):
    body = await request.json()
    data_source_conf = {{
        'driver_class': '{ds_conf['driver_class']}',
        'url': '{ds_conf['url']}',
        'username': '{ds_conf['username']}',
        'password': '{ds_conf['password']}'
    }}
    sql = r\"\"\"{sql}\"\"\"
    result = await database_exchange.exec_select(data_source_conf, sql, body)
    return JSONResponse(R.success(result).model_dump())
"""
    return code, handler_name
