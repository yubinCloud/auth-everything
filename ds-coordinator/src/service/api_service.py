import copy
import time

from service.oracle_serivices import QueryBuilder
from service.ds_worker_api import DsWorker
from service.avue_api import AvueApi
from schema.requ.sql_request import create_sql_conn
from service.sql_service import ConnObj

DBWorker = DsWorker()
Avue = AvueApi()

def get_conn_info():
    conn_obj_id = "1696752360028"
    # conn_obj = ConnObj({
    #     "driverClass": "oracle.jdbc.driver.OracleDriver",
    #     "url": "jdbc:oracle:thin:@192.168.255.89/orcl",
    #     "username": "wt2018",
    #     "password": "wt2018wt2018",
    #     "name": "gt"
    # })
    base_conn_info = {
        # "driverClass": "oracle.jdbc.driver.OracleDriver",
        "driverClass": 3,
        "url": "jdbc:oracle:thin:@192.168.255.89/orcl",
        "username": "wt2018",
        "password": "wt2018wt2018",
        "name": "gt"
    }
    conn_obj = ConnObj(create_sql_conn(**base_conn_info))
    return conn_obj


async def base_api(sql):
    conn_obj = get_conn_info()
    result = await DBWorker.exec_sql(conn_obj, sql)
    return result


async def get_gt_gsxx(uniscid, entname):
    '''
    查询个体工商信息.允许使用工商注册号或企业名查询
    '''
    search_sql = QueryBuilder.get_gt_gsxx_sql(uniscid, entname)
    if not search_sql:
        return {"code":-1,"msg":"缺少参数","data":[]}
    result = await base_api(search_sql)
    return result

async def get_qy_gsxx(uniscid, entname):
    '''
    查询企业工商信息.允许使用工商注册号或企业名查询
    '''
    search_sql = QueryBuilder.get_qy_gsxx_sql(uniscid, entname)
    if not search_sql:
        return {"code":-1,"msg":"缺少参数","data":[]}
    result = await base_api(search_sql)
    return

async def get_ssxx(uniscid, entname):
    '''
    查询社保信息.允许使用参保单位或统一社会信用代码查询
    '''
    search_sql = QueryBuilder.get_ssxx_sql(uniscid, entname)
    if not search_sql:
        return {"code":-1,"msg":"缺少参数","data":[]}
    result = await base_api(search_sql)
    return result

async def get_qyzl(uniscid, entname):
    '''
    查询企业质量信息.允许使用参保单位或统一社会信用代码查询
    '''
    search_sql = QueryBuilder.get_qyzl_sql(uniscid, entname)
    if not search_sql:
        return {"code":-1,"msg":"缺少参数","data":[]}
    result = await base_api(search_sql)
    return result

async def get_nsls(uniscid, entname):
    '''
    查询纳税历史信息.允许使用参保单位或统一社会信用代码查询
    '''
    search_sql = QueryBuilder.get_nsls_sql(uniscid, entname)
    if not search_sql:
        return {"code":-1,"msg":"缺少参数","data":[]}
    result = await base_api(search_sql)
    return result


async def get_ryjy(zjhm, start_date, end_date):
    """
    查询人员就医数据
    """
    try:
        query_total, query_yy_total, query_rc = QueryBuilder.get_ryjy_sql(zjhm, start_date, end_date)
    except Exception as e:
        print("### ryjy service:", e)
        return {"code": -1, "msg": "缺少参数", "data": {}}
    response = {
        "code": 0,
        "msg": "success",
        "data": {}
    }
    total_response = await base_api(query_total)
    if total_response["code"] != -1:
        response["data"]["total"] = total_response["data"][0]["TOTAL"]
    else:
        print(total_response)
        response["msg"] += total_response["msg"]
        response["code"] = -1

    yy_total_response = await base_api(query_yy_total)
    if yy_total_response["code"] != -1:
        response["data"]["group_jgbh"] = yy_total_response["data"]
    else:
        print(yy_total_response)
        response["code"] = -1
        response["msg"] += yy_total_response["msg"]

    query_rc = await base_api(query_rc)
    if query_rc["code"] != -1:
        try:
            response["data"]["total_person"] = query_rc["data"][0]["TOTAL"]
        except Exception as e:
            print("#### ryjy person error: ", e)
            response["data"]["total_person"] = -1
    else:
        print(query_rc)
        response["code"] = -1
        response["msg"] += query_rc["msg"]

    return response


async def get_qyjbxx(uniscid, entname):
    '''
    查询 企业基本信息
    '''
    search_sql = QueryBuilder.get_qyjbxx_sql(uniscid, entname)
    if not search_sql:
        return {"code":-1, "msg":"缺少参数/参数错误", "data":[]}
    result = await base_api(search_sql)
    return result


async def get_qyjjyxqk(uniscid, entname):
    '''
    查询 企业经济运行情况
    '''
    search_sql = QueryBuilder.get_qyjjyxqk_sql(uniscid, entname)
    if not search_sql:
        return {"code":-1, "msg":"缺少参数/参数错误", "data":[]}
    result = await base_api(search_sql)
    return result


async def get_qyzlxx(uniscid, entname):
    '''
    查询纳税历史信息.允许使用参保单位或统一社会信用代码查询
    '''
    search_sql = QueryBuilder.get_qyzlxx_sql(uniscid, entname)
    if not search_sql:
        return {"code":-1, "msg":"缺少参数/参数错误", "data":[]}
    result = await base_api(search_sql)
    return result


async def get_jyxx(qhdm:list, start_date:str, end_date:str):
    '''
    查询区划代码列表内的就医信息。
    '''
    allTotal,allJyrs,allJyrc,jgpm = QueryBuilder.get_jyxx_sql(qhdm, start_date, end_date)
    response = {
        "code": 0,
        "msg": "success",
        "data": {}
    }
    # 处理总人数
    allTotal_response = await base_api(allTotal)
    if allTotal_response["code"] != -1:
        response["data"]["allTotal"] = allTotal_response["data"][0]["TOTAL"]
        # response["data"]["allJyrs"] = allTotal_response["data"][0]["TOTAL"]
    else:
        print("#### jyxx: allTotal_response", allTotal_response)
        response["data"]["allTotal"] = -1
        # return packJyxxResponse(response, "查询总人数失败", -1)
    # 处理就医人数
    allJyrs_response = await base_api(allJyrs)
    if allJyrs_response["code"] != -1:
        response["data"]["allJyrs"] = allJyrs_response["data"][0]["TOTAL"]
    else:
        print("#### jyxx: allTotal_response", allJyrs_response)
        return packJyxxResponse(response, "查询总就医人数失败", -1)

    # 处理就医人次
    allJyrc_reponse = await base_api(allJyrc)
    if allJyrc_reponse["code"] != -1:
        response["data"]["allJyrc"] = allJyrc_reponse["data"][0]["TOTAL"]
    else:
        print("#### jyxx: allJyrc_reponse", allJyrc_reponse)
        return packJyxxResponse(response, "查询就医人次失败", -1, response["data"])
    jgpm_response = await base_api(jgpm)
    if jgpm_response["code"] != -1:
        response["data"]["jgpm"] = jgpm_response["data"]
    else:
        print("#### jyxx: jgpm_response", jgpm_response)
        return packJyxxResponse(response, "查询机构排名失败", -1, response["data"])
    # 计算占比
    first_yy = 0
    if len(response["data"]["jgpm"]) >= 1:
        first_yy = response["data"]["jgpm"][0]["JYRC"]
    for item in response["data"]["jgpm"]:
        item["BAR"] = round(item["JYRC"] / first_yy, 4)
        item["ZB"] = round(item["JYRC"] / response["data"]["allJyrc"], 4)

    return packJyxxResponse(response, "success", 0, response["data"])


async def get_qymcbg(uniscid):
    """
    企业名称变更记录
    """
    qymc_sql, cym_sql = QueryBuilder.get_qymcbg_sql(uniscid)
    response = {
        "code": 0,
        "msg": "success",
        "data": {}
    }
    qymc_resp = await base_api(qymc_sql)
    print("qymc ds-worker response", qymc_resp)
    qymc_sql_result = qymc_resp["data"] if qymc_resp["code"] != -1 else {"QYMC": None}
    response["data"].update(qymc_sql_result[0] if len(qymc_sql_result) else {"QYMC": "undefind"})

    cym_resp = await base_api(cym_sql)
    print("cym response:", cym_resp)
    cgbl_sql_result = cym_resp["data"] if cym_resp["code"] != -1 else {"CYM": []}
    response["data"].update({"CYM": cgbl_sql_result})
    return response

async def get_qygdtzzb(uniscid):
    """
    企业股东投资占比
    """
    qymc_sql, cgbl_sql = QueryBuilder.get_qygdtzzb_sql(uniscid)
    response = {
        "code": 0,
        "msg": "success",
        "data": {}
    }
    qymc_resp = await base_api(qymc_sql)
    print("qymc ds-worker response", qymc_resp)
    qymc_sql_result = qymc_resp["data"] if qymc_resp["code"] != -1 else {"QYMC": None}
    response["data"].update(qymc_sql_result[0] if len(qymc_sql_result) else {} )

    cgbl_resp = await base_api(cgbl_sql)
    print("cym response:", cgbl_resp)
    cgbl_sql_result = cgbl_resp["data"] if cgbl_resp["code"] != -1 else {"CGBL": []}
    response["data"].update({"CGBL":cgbl_sql_result})
    return response

async def get_result_from_page(page, per_page, table_name):
    response = {
        "code": 0,
        "msg": "",
        "data": {}
    }
    sql = QueryBuilder('oracle.jdbc.OracleDriver').build_query(table_name, page, per_page)
    table_data = await base_api(sql)
    if response["code"] != 0:
        response["code"] = -1
        response["msg"] += "获取数据错误"
        response["data"]["data"] = []
    else:
        response["data"]["data"] = table_data["data"]

    total_num = await base_api(f"select count(*) as COUNT from {table_name}")
    if response["code"] != 0:
        response["code"] = -2
        response["msg"] += "获取总数错误"
        response["data"]["total_num"] = -1
    else:
        response["data"]["total_num"] = total_num["data"][0]["COUNT"]
    return response

async def get_data_from_uniscid_page(table_name, form, page_number, page_size):
    response = {
        "code": 0,
        "msg": "",
        "data": {}
    }
    if form.uniscid:
        filter_sql = f"xzxdrdm = '{form.uniscid}'"
    elif form.entname:
        filter_sql = f"xzxdrmc = '{form.entname}'"
    else:
        return packJyxxResponse(response, "请求缺少参数", -1)

    sql = f"""SELECT *
             FROM (
                 SELECT {table_name}.*, ROWNUM AS rn
                 FROM {table_name}
                 WHERE {filter_sql}
             )
             WHERE rn BETWEEN ({page_number} - 1) * {page_size} + 1 AND {page_number} * {page_size}"""
    table_data = await base_api(sql)

    if response["code"] != 0:
        response["code"] = -1
        response["msg"] += "获取数据错误"
        response["data"]["data"] = []
    else:
        response["data"]["data"] = table_data["data"]

    total_num = await base_api(f"select count(*) as COUNT from {table_name} where {filter_sql}")
    if response["code"] != 0:
        response["code"] = -2
        response["msg"] += "获取总数错误"
        response["data"]["total_num"] = -1
    else:
        response["data"]["total_num"] = total_num["data"][0]["COUNT"]
    return response


async def get_mid_yydd(page, per_page):
    """
    mid_yydd
    """
    table_name = "WT2018.MID_YYDD"
    return await get_result_from_page(page, per_page, table_name)


async def get_mid_xzjl(form, page_num, page_size):
    """
    mid_xzjl
    """
    table_name = "WT2018.MID_XZJL"
    return await get_data_from_uniscid_page(table_name, form, page_num, page_size)



async def get_mid_xzqz(form, page_num, page_size):
    """
    mid_xzqz
    """
    table_name = "WT2018.MID_XZQZ"
    return await get_data_from_uniscid_page(table_name, form, page_num, page_size)



def packJyxxResponse(response:dict,msg:str,code:int, data=None):
    response["msg"] = msg
    response["code"] = code
    response["data"] = data if data else {}
    return response



if __name__ == '__main__':
    print(get_conn_info())