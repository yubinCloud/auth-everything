import copy
import time

from service.oracle_serivices import QueryBuilder
from service.ds_worker_api import DsWorker
from service.avue_api import AvueApi

from service.es_api import EsDbSave, EsDbSearch

DBWorker = DsWorker()
Avue = AvueApi()

class ConnObj:
    def __init__(self, form):
        if not form:
            raise ValueError("# init conn obj form is None")
        self.driverClass = form['driverClass']
        self.url = form['url']
        self.username = form['username']
        self.password = form['password']
        self.name = form["name"] if form.get("name") else "default"

    @classmethod
    def init(cls, form):
        # 根据form中的信息初始化自身，返回对应的ConnObj
        return cls(form)

def make_response(data, msg=None):
    '''
    任何有表示为真的data，都作为success响应。反之为error
    '''
    if data:
        return {"code": 0, "msg": "success", "data": data}
    return {"code": -1, "msg": f"error; {msg}", "data": data}


async def create_sql_conn(info: dict, header:dict):
    '''
    创建/添加sql连接,
    '''
    if not info["remark"]:
        del info["remark"]
    return Avue.db_save(None, info, header)


async def test_sql_conn(form: dict):
    ''' 测试连接 '''
    # if form["driverClass"] == "com.mysql.cj.jdbc.Driver":
    #     return Avue.db_test(form)
    # else:
    #     test_sql = ""
    conn_obj = ConnObj(form)
    if conn_obj.driverClass in ["com.mysql.cj.jdbc.Driver", "X-Inceptor"]:
        test_sql = "select 1"
    else:
        test_sql = "select 1 from dual"
    resutl = await DBWorker.exec_sql(conn_obj, test_sql)
    return resutl


async def get_sql_conn_list(page: dict, headers:dict):
    ''' 获取sql连接列表 '''
    page_num = page["page_num"]
    page_size = page["page_size"]
    return Avue.db_list(page_num, page_size, headers)


async def get_sql_conn_detail(conn_id: str, headers:dict):
    ''' 获取sql连接详情 '''
    return Avue.db_detail(conn_id, headers)


async def update_sql_conn( conn_id: str, info: dict, headers:dict):
    ''' 更新sql连接 '''
    return Avue.db_save(conn_id, info, headers)


async def delete_sql_conn(id: str, headers:dict):
    ''' 删除sql连接 '''
    return Avue.db_remove(headers=headers, id=id)


async def get_sql_table_list(conn_id: str, headers:dict)->tuple:
    ''' 获取数据库的数据表名列表 '''
    try:
        db_conn_response = Avue.db_detail(conn_id, headers)
    except:
        return [], "链接avue获取数据源信息时发生错误"
    if not db_conn_response.get('data'):
        return [], db_conn_response["msg"]
    conn_obj = ConnObj(db_conn_response.get('data'))
    retsult = await DBWorker.get_table_list(conn_obj)
    return retsult


async def get_sql_table_detail(conn_id: str, table_name: str, headers:dict,store=False) -> tuple:
    '''
    获取sql表详情(字段名)
    refresh: 控制是否重新获取
    '''
    # table_info = EsDbSearch().get_fields(conn_id, table_name, headers)
    # if table_info and not refresh:
    #     field = [item["field_name"] for item in table_info.get("field_list", [])]   # 从es中获取
    #     print("# 已缓存，不再获取：", table_name)
    #     return field, None
    # else:
    #     print("# 未找到或未缓存数据表信息，next:开始缓存")
    avue_response = Avue.db_detail(conn_id, headers)
    db_conn_obj = ConnObj(avue_response["data"]) if avue_response.get("data") else None
    if not db_conn_obj:
        return [], f"链接有误，{str(avue_response)}"

    table_fields_all_info = await DBWorker.get_table_field(
        db_conn_obj,
        table_name
    )
    if table_fields_all_info["code"] == 0:
        if db_conn_obj.driverClass == "X-Inceptor":
            field = [item["column_name"] for item in table_fields_all_info["data"]]
        else:
            field = [item["COLUMN_NAME"] for item in table_fields_all_info["data"]]
        # =========es=======
        # 保存数据表的字段信息
        # if not field:
        #     print("# 无字段信息，不缓存")
        #     return field, '查询语法导致的错误'
        # try:
        #     if store:
        #         data = {
        #             "conn_id": conn_id,
        #             "conn_name": db_conn_obj.name,
        #             "table_name": table_name,
        #             "owner": user,
        #             "field_list": table_fields_all_info["data"]}
        #         # TODO: 鉴别是否已存在. 当前由于首先获取了数据进行判定，所以不会重复存储
        #         result = EsDbSave().store_data(data, ignore_check=True)
        #         print("保存数据表的字段信息进度", result)
        #     else:
        #         print("'store=False'不保存数据表的字段信息")
        # except Exception as e:
        #     print("保存数据表的字段信息失败",e)
        try:
            if store:
                data = {
                        "conn_id": conn_id,
                        "conn_name": db_conn_obj.name,
                        "table_name": table_name,
                        "owner": headers["user"],
                        "field_list": table_fields_all_info["data"]
                }
                # TODO: 鉴别是否已存在. 当前由于首先获取了数据进行判定，所以不会重复存储
                result = EsDbSave().store_data(data, ignore_check=True)
                print("保存数据表的字段信息进度", result)
        except Exception as e:
            print(f"# store field error:{e}")

        return field, None
    else:
        print("ds_worker:", table_fields_all_info)
        return [], str(table_fields_all_info)


async def get_sql_table_data(conn_id: str, headers:dict, table_name: str, page_info) -> (str, bool):
    '''
    获取sql表数据
    '''
    db_conn_obj = ConnObj(Avue.db_detail(conn_id, headers)["data"])
    if not db_conn_obj:
        return f"连接不存在", False
    # 拼凑sql语句，通过jdbc获取数据
    # jdbc_sql = DsWorker.format_sql(db_class=db_conn_obj.driverClass, table_name=table_name, page_info=page_info)
    # 针对oracle 12c之前的版本
    # jdbc_sql = DsWorker.format_sql(db_class=db_conn_obj.driverClass,
    #                                          table_name=table_name, page_info=page_info, oracle_12c_after=True)

    #
    jdbc_sql = QueryBuilder(database_type=db_conn_obj.driverClass).build_query(table_name, page_info['page_num'],
                                                               page_info['page_size'])

    response = await DBWorker.exec_sql(
        db_conn_obj,
        jdbc_sql,
        sub_name="data"
    )

    if response["code"] != 0:
        return {"code": -1, "msg": f"error; {response['msg']}", "data": None}

    # 检查response是否为error，否则使用oracle_12c_after传入构建

    # 获取数据总数
    count_sql = f"select count(*) as count from {table_name}"
    count_response = await DBWorker.exec_sql(
        db_conn_obj,
        count_sql,
        sub_name="count"
    )
    result = copy.deepcopy(response["data"]) if response.get("data",None) else []
    try:
        count_ = QueryBuilder.get_count(db_conn_obj.driverClass, count_response)
    except:
        count_ = 0
    response["data"] = {"query_set": result if result else [], "count": count_}
    return response



async def search_keyword( key_word:str, page_info, headers:dict) -> (str, bool):
    '''执行es搜索，不再检查缓存数据情况，直接进行搜索'''
    page, page_size = page_info["page_num"], page_info["page_size"]
    # try:
    result,count = EsDbSearch().kw_search(key_word, page, page_size, headers)
    return {"code":0, "msg": "success", "data": {"count": count, "search_result": result}}
    # except Exception as e:
    #     return {"code":-1, "msg": f"error; {e}", "data": None}

async def background_get_data_source_info(conn_id_list:list, headers:dict):
    """后台获取数据源信息"""
    for conn_id in conn_id_list:
        table_list, error = await get_sql_table_list(conn_id, headers)
        # 记录开始时间
        with open("cache_dataSource_log.txt", "a", encoding="utf-8") as f:
            f.write(f"{time.strftime('%Y-%m-%d %H:%M:%S', time.localtime())}@{conn_id}")

        if not table_list:
            with open("cache_dataSource_log.txt", "a", encoding="utf-8") as f:
                f.write(f"@获取数据源信息失败{error}")
            print("获取数据源信息失败", conn_id, error)

        for table in table_list:
            # 获取每个表的字段列表
            if "SYS_NTGjqJ" not in table:
                table_detail, error = await get_sql_table_detail(conn_id, table, headers=headers, store=True)
                if error:
                    print(error)
                    continue
            # 记录结束时间
        with open("cache_dataSource_log.txt", "a", encoding="utf-8") as f:
            f.write(f"@{time.strftime('%Y-%m-%d %H:%M:%S', time.localtime())}\n")



async def get_dataSource_cache_log(header:dict):
    """获取数据源缓存日志"""
    try:
        with open("cache_dataSource_log.txt","r",encoding="utf-8") as f:
            log = f.readlines()
        # 滚动保留最后3组日志
        if len(log) >= 6:
            with open("cache_dataSource_log.txt","w",encoding="utf-8") as f:
                f.writelines(log[-6:])
        log = [item.strip() for item in log]
        last_log = log[-1]
        if "@" not in last_log:
            raise ValueError("日志文件最后一行格式错误")
        last_log = last_log.split("@")
        # 最少应该有一个@，除非未完成任务
        if 2 <= len(last_log) < 4:
            return {"code": 0, "msg":"success","data":{"start_time": last_log[0], "end_time": "未结束", "status": 0,"conn_id":last_log[1]}}
        elif len(last_log) == 4:
            return {"code": 0, "msg":"success","data":{"start_time": last_log[0], "end_time": last_log[-1], "status": 1,"conn_id":last_log[1]}}

    except FileNotFoundError as e:
        print("dataSource log: ", e)
        return {"code":-1, "msg": "日志文件无效", "data": {"status":-1}}
    except Exception as e:
        print("# dataSource log: ", e)
        return {"code":-1, "msg": "日志文件无效", "data": {"status":-1}}



