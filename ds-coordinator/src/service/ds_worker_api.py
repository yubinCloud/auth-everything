import datetime
import random
import sys
import httpx

import requests
from config import settings
from service.oracle_serivices import QueryBuilder


class DsWorker():
    def __init__(self):
        pass

    def get_url(self, uri):
        return f"http://{settings.other_ds_worker_host}{uri}"

    async def base_request(self, method, url, payload, header=None, timeout=5):
        '''基本的request请求，所有请求都从这里发出'''
        headers = {
            'Content-Type': 'application/json'
        }
        if header:
            headers.update(header)
        new_time = datetime.datetime.now() + datetime.timedelta(minutes=14)
        formatted_time = new_time.strftime('%Y-%m-%d %H:%M:%S')
        print("# 请求时间：", formatted_time, "(非生产环境下需-14分钟即为实际时间)")
        print("# ds-work url: ", url)
        print("# ds-work payload: ", payload)

        error_list = []
        async with httpx.AsyncClient() as client:
            if method == "get":
                res = await client.get(url, headers=headers, params=payload, timeout=timeout)
            elif method == "post":
                res = await client.post(url, headers=headers, json=payload, timeout=timeout)
            else:
                return {"code": -1, "msg": f"请联系开发人员检查ds-worker服务"}
        try:
            print("ds-worker response:", res.json())
        except Exception as e:
            print("ds-worker api error:",e)
        # 根据code处理

        if res.status_code == 200:
            return res.json()
        elif res.status_code == 400:
            response = res.json()
            msg = response.get("msg", "")
            if "25191" in msg:
                return {"code": -1, "msg": f"请检查数据表父表授权信息"}
            elif "01005" in msg:
                return {"code": -1, "msg": f"请检查账号密码"}
            elif "00933" in msg:
                return {"code": -1, "msg": "查询语法错误"}
            else:
                print("# ds-worker 400:", response)
                return {"code": -1, "msg": f"请检查数据库信息。账号密码、授权。"}
        else:
            print("## ds-worker status_code: ",res.status_code)
            print(res.text)
            return {"code": -1, "msg": f"ds-worker请求失败"}

    def get_payload(self, conn_obj, table_name=None, sql=None, is_table_list=False, head_name="", sub_name="",
                    remove_id=False):
        '''
        获取请求用的payload
        :param conn_obj: 连接对象
        :param table_name: 表名
        :param sql: sql语句
        :param is_table_list: 是否是获取表列表
        :param head_name: 头部名称
        :param sub_name: 子部名称
        :param remove_id: 是否移除id
        :return: payload
        '''
        # 获取当前函数名
        # func_name = sys._getframe().f_code.co_name
        conn_payload = {
            # "id": f"avue:{conn_obj.id}{head_name}{sub_name}",
            "id": None,
            "driverClass": conn_obj.driverClass,
            "url": conn_obj.url,
            "username": conn_obj.username,
            "password": conn_obj.password,
        }
        if remove_id:
            conn_payload["id"] = None
        if is_table_list:
            return conn_payload
        if table_name:
            return {"dataSourceConf": conn_payload, "tableName": table_name}
        if sql:
            return {"dataSourceConf": conn_payload, "sql": sql}

        return None

    async def get_table_list(self, conn_obj, header=None):
        uri = "/meta/tables"
        url = self.get_url(uri)
        data = self.get_payload(conn_obj, is_table_list=True, head_name=f"{sys._getframe().f_code.co_name}")
        response = await self.base_request("post", url, data, header=header)
        if response.get("code", None) == 0:
            if conn_obj.driverClass == "oracle.jdbc.OracleDriver":
                return [f"{item['TABLE_SCHEM']}.{item['TABLE_NAME']}" for item in response["data"]], ""
            elif conn_obj.driverClass == "X-Inceptor":
                return [item["table_name"] for item in response["data"]], ""
            else:
                return [item["TABLE_NAME"] for item in response["data"]], ""
        else:
            return [], str(response)

    async def get_table_field(self, conn_obj, table_name, header=None):
        uri = "/meta/fields"
        url = self.get_url(uri)
        remove_id = False
        if conn_obj.driverClass == "oracle.jdbc.OracleDriver":
            table_name = table_name.split(".")[-1]
        while True:
            data = self.get_payload(conn_obj, table_name, head_name=f"{sys._getframe().f_code.co_name}",
                                    remove_id=remove_id)
            print("ds-worker payload", data)
            response = await self.base_request("post", url, data, header=header)
            if response["code"] == -400:
                remove_id = True
                continue
            elif response["code"] == 0:
                break
            else:
                print("# ds get_table_field: ", response)
                break
        return response

    @staticmethod
    def format_sql(db_class, table_name, page_info=None, oracle_12c_after=False):
        # 主要针对oracle的sql进行格式化
        sql = QueryBuilder(database_type=db_class).build_query(table_name, page_info['page_num'],
                                                               page_info['page_size'], oracle_12c_after)
        return sql

    async def exec_sql(self, db_conn_obj, sql, sub_name="", header=None):
        uri = "/exec/select"
        url = self.get_url(uri)
        data = self.get_payload(db_conn_obj, sql=sql, head_name=f"{sys._getframe().f_code.co_name}", sub_name=sub_name)
        response = await self.base_request("post", url, data, timeout=120, header=header)
        return response


if __name__ == '__main__':
    # 初始化异步运行环境
    import asyncio

    loop = asyncio.get_event_loop()
    ds_worker = DsWorker()
    # print(loop.run_until_complete(get_domain_ssl_detail("114.242.19.134")))
    print(loop.run_until_complete(ds_worker.base_request('get', url='http://www.baidu.com', payload=None,header=None)))



    # ds_worker = DsWorker()
    # data = {
    #     "driverClass": "com.mysql.cj.jdbc.Driver",
    #     "db_url": "jdbc:mysql://10.245.142.253:3307/db0",
    #     "username": "root",
    #     "password": "root"
    # }
    # response = await ds_worker.base_request('get', url='http://www.baidu.com', payload=None)
    # print(response)
