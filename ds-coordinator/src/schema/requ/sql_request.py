import random
from fastapi import Query


def create_sql_conn(
        name: str = Query(..., title='连接名'),
        driverClass: int = Query(
            ...,
            title='连接类型',
            ge=1,
            le=5,
            description="1:com.mysql.cj.jdbc.Driver / "
                        "2: org.postgresql.Driver / "
                        "3:oracle.jdbc.OracleDriver / "
                        "4: com.microsoft.sqlserver.jdbc.SOLServerDriver/"
                        "5: X-Inceptor"
        ),
        username: str = Query(..., title='数据库的用户名'),
        password: str = Query(..., title='数据库的密码'),
        url: str = Query(..., title='连接地址', description="前端传输内容为：ip:port/database；jdbc:mysql://不发送"),
        remark: str = Query(title='备注', default=None),
):

    # 格式化source_type字段为驱动器
    if driverClass == 1:
        driver = "com.mysql.cj.jdbc.Driver"
        # conn_type = "mysql://"
    elif driverClass == 2:
        driver = "org.postgresql.Driver"
        # conn_type = "postgresql"
    elif driverClass == 3:
        driver = "oracle.jdbc.OracleDriver"
        # conn_type = "oracle:thin:@"
    elif driverClass == 4:
        driver = "com.microsoft.sqlserver.jdbc.SOLServerDriver"
    elif driverClass == 5:
        driver = "X-Inceptor"
    else:
        driver = "unknow"

    return {
        "name": name,
        "driverClass": driver,
        "username": username,
        "password": password,
        "url": f"{url}",
        "remark": remark
    }


def page_query(
        page_num: int = Query(..., title='page_num'),
        page_size: int = Query(..., title='page_size'),
        ):
    return {
        "page_num": page_num,
        "page_size": page_size,
    }