from typing import List, Annotated, Union

import aioredis
from fastapi import APIRouter, Depends, Query, Header
from schema.resp import RestfulModel
from schema.resp import sql_detail
from schema.requ import sql_request

from service import sql_service, api_service
from dependencies import redis_cache, use_redis_client, role_check


api_router = APIRouter(tags=['数据库查询接口'],prefix="/shared-data")

@api_router.post("/gt_gsxx/",
                summary='查询个体工商信息',
                response_model=RestfulModel,
                description="查询工商信息.允许使用工商注册号或企业名查询"
                )
async def get_gt_gsxx(
    form: sql_detail.GtGsxx,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
    ):
    print(form)
    result = await api_service.get_gt_gsxx(form.uniscid, form.entname)
    print("# gtgsxx api response:",result)
    return RestfulModel.response(result)

@api_router.post("/ent_gsxx/",
                summary='查询企业工商信息',
                response_model=RestfulModel,
                description="查询工商信息.允许使用工商注册号或企业名查询"
                )
async def get_gsxx(
    form: sql_detail.EntGsxx,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_qy_gsxx(form.uniscid, form.entname)
    print("# qygsxx api response:",result)
    return RestfulModel.response(result)

@api_router.post("/ssxx/",
                 summary='查询社保信息',
                 response_model=RestfulModel,
                 description="查询社保信息.允许使用参保单位或统一社会信用代码查询")
async def get_ssxx(
    form: sql_detail.EntGsxx,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_ssxx(form.uniscid, form.entname)
    print("# sbxx api response:",result)
    return RestfulModel.response(result)


@api_router.post("/qyzl/",
                 summary='查询企业专利信息',
                 response_model=RestfulModel,
                 description="查询企业专利信息.允许使用参保单位或统一社会信用代码查询")
async def get_qyzl(
    form: sql_detail.EntGsxx,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_qyzl(form.uniscid, form.entname)
    print("# qyzl api response:",result)
    return RestfulModel.response(result)


@api_router.post("/nsls/",
                summary='查询税收信息',
                response_model=RestfulModel,
                description="查询税收信息.允许使用参保单位或统一社会信用代码查询")
async def get_nsls(
    form: sql_detail.EntGsxx,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_nsls(form.uniscid, form.entname)
    print("# nsls api response:",result)
    return RestfulModel.response(result)



@api_router.post("/ryjy/",
                summary='查询人员就医',
                response_model=RestfulModel,
                description="查询人员就医.允许使用医疗机构编码和人员证件号码查询")
async def get_ryjy(
    form: sql_detail.Ryjy,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_ryjy(form.zjhm, form.start_date, form.end_date)
    print("# ryjy api response:",result)
    return RestfulModel.response(result)


@api_router.post("/qyjbxx/",
                summary='查询企业基本信息',
                response_model=RestfulModel,
                description="查询企业基本信息。企业名称/统一社会信用代码")
async def get_qyjbxx(
    form: sql_detail.EntGsxx,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_qyjbxx(form.uniscid, form.entname)
    print("# qyjbxx api response:",result)
    return RestfulModel.response(result)



@api_router.post("/qyjjyxqk/",
                summary='查询企业经济运行情况',
                response_model=RestfulModel,
                description="查询企业经济运行情况。企业名称")
async def get_qyjjyxqk(
    form: sql_detail.EntGsxx,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_qyjjyxqk(form.uniscid, form.entname)
    print("# qyjjyxqk api response:",result)
    return RestfulModel.response(result)


@api_router.post("/qyzlxx/",
                summary='查询企业专利信息',
                response_model=RestfulModel,
                description="查询企业专利信息。企业名称")
async def get_qyzlxx(
    form: sql_detail.EntGsxx,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_qyzlxx(form.uniscid, form.entname)
    print("# qyzlxx api response:",result)
    return RestfulModel.response(result)


@api_router.post("/jyxx/",
                summary='查询就医信息(财政综合业务app)',
                response_model=RestfulModel,
                description="查询企业专利信息。企业名称")
async def get_qyzlxx(
    form: sql_detail.Jyxx,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_jyxx(form.qhdm, form.start_date, form.end_date)
    print("# qyzlxx api response:",result)
    return RestfulModel.response(result)


# 工信企业信息库
@api_router.post("/qymcbg/",
                summary='查询企业名称变更',
                response_model=RestfulModel)
async def get_qymcbg(
    form: sql_detail.EntGsxx,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_qymcbg(form.uniscid)
    print("# get_jymcbg api response:",result)
    return RestfulModel.response(result)

@api_router.post("/qygdtzzb/",
                summary='查询企业股东投资占比',
                response_model=RestfulModel)
async def get_qymcbg(
    form: sql_detail.EntGsxx,
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_qygdtzzb(form.uniscid)
    print("# get_qygdtzzb api response:",result)
    return RestfulModel.response(result)

@api_router.post("/mid_yydd/",
                summary='查询mid_医院信息',
                response_model=RestfulModel)
async def get_mid_yydd(
    page: sql_request.page_query = Depends(sql_request.page_query),
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    page_num = page["page_num"]
    page_size = page["page_size"]
    result = await api_service.get_mid_yydd(page_num, page_size)
    print("# mid_yydd api response:",result)
    return RestfulModel.response(result)


@api_router.post("/mid_xzqz/",
                summary='查询mid_xzqz',
                response_model=RestfulModel)
async def get_mid_xzqz(
    form: sql_detail.EntGsxx,
    page: sql_request.page_query = Depends(sql_request.page_query),
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_mid_xzqz(form, page["page_num"], page["page_size"])
    print("# mid_xzqz api response:",result)
    return RestfulModel.response(result)


@api_router.post("/mid_xzjl/",
                summary='查询 xzjl',
                response_model=RestfulModel)
async def get_mid_xzjl(
    form: sql_detail.EntGsxx,
    page: sql_request.page_query = Depends(sql_request.page_query),
    header: sql_request.custom_header = Depends(sql_request.custom_header),
):
    result = await api_service.get_mid_xzjl(form, page["page_num"], page["page_size"])
    print("# mid_xzjl api response:",result)
    return RestfulModel.response(result)