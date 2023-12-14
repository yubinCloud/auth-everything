from typing import Tuple, Union
import re

from dynamic_route import RouteId
from exchange.ds_worker_exchange import DatasourceConf
from exception import RequestParamsException
from schema.meta import ParamFieldType, typed_map


def create_sql_api_handler(
        route_id: RouteId,
        sql: str,
        ds_conf: DatasourceConf
) -> Tuple[str, str]:
    handler_name = f'Endpoint_{route_id}'
    body_clz_code, body_obj_code = _create_sql_api_body(route_id, sql)
    code = f"""class {handler_name}(HTTPEndpoint):
    {body_clz_code}

    async def post(self, request):
        {body_obj_code}
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
    print(code)
    return code, handler_name


def  _parse_sql_slot(slot: str):
    print('slot: ', slot)
    slot = slot[2:-1]
    splits = slot.split(',')
    if splits:
        var_name = splits[0].strip()
    else:
        raise RequestParamsException(message=r'SQL 中 #{} 内参数名称不允许为空')
    typed = ParamFieldType.STRING
    if len(splits) > 1:
        annos = splits[1].strip().split('=')
        if len(annos) < 2 or annos[0] != 'type':
            raise RequestParamsException(message=r'SQL 中 #{} 内参数类型注解格式不正确，示例：`#{year,type=int}`')
        typed = typed_map.get(annos[1], None)
        if typed is None:
            raise RequestParamsException(f'参数 {var_name} 的类型标注不符合要求')
    return {
        'name': var_name,
        'typed': typed
    }


def _create_sql_api_body(
    route_id: str,
    sql: str
) -> Union[str, str]:
    SLOT_PATTERN = re.compile(r'#\{.+?\}')
    body_fields = []
    for slot in re.finditer(SLOT_PATTERN, sql):
        slot_info = _parse_sql_slot(slot.group())
        field = f"{slot_info['name']}: {slot_info['typed'].value}"
        body_fields.append(field)
    if not body_fields:
        return "", "body = None"
    body_fields_str = '        \n'.join(body_fields)
    return f"""class Body(BaseModel):\n        {body_fields_str}""", "body = self.Body.model_validate(await request.json())"