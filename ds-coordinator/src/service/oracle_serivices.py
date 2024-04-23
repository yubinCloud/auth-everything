from sqlalchemy import text
from sqlalchemy.sql import select


# 查询接口的数据表映射
class TableMap():
    table_space = "WT2018"

    gt_gsxx = f"{table_space}.MID_GTGSH"  # 个体工商信息
    qy_gsxx = f"{table_space}.MID_GSXX" # 企业工商信息
    ssxx = f"{table_space}.MID_SSXX"  # 社保信息
    qyzl = f"{table_space}.MID_QYZL"  # 企业专利
    nsls = f"{table_space}.MID_NSLS"  # 企业专利
    ryjy = f"{table_space}.MID_RYJY_TMP"  # 人员就医
    qyjbxx = f"{table_space}.MID_QYJBXX"  # 企业基本信息
    qyjjyxqk = f"{table_space}.MID_JJYXQK"  # 企业经济运行情况
    qyzlxx = f"{table_space}.MID_QYZLQK"  # 企业专利情况
    jyxx = f"{table_space}.MID_JYXX"  # 医院信息,提供的需求中的表名为MID_YYXX
    rkxx = f"{table_space}.JMZHXXB"  # 人口信息。接口需求侧同上
    qymcbg = f"{table_space}.MID_QYMCBG"  # 工信接口
    qygdtzzb = f"{table_space}.MID_QYGDTZZB"  # 工信接口


class QueryBuilder:
    # 只用于sql获取分页数据和计数
    def __init__(self, database_type):
        self.database_type = database_type

    def build_query(self, table_name, page, per_page):
        if self.database_type == 'oracle.jdbc.OracleDriver':
            query = self._build_oracle_query(table_name, page, per_page)
        elif self.database_type == 'com.mysql.cj.jdbc.Driver':
            query = self._build_mysql_query(table_name, page, per_page)
        elif self.database_type == 'org.postgresql.Driver':
            query = self._build_postgres_query(table_name, page, per_page)
        elif self.database_type == 'com.microsoft.sqlserver.jdbc.SQLServerDriver':
            query = self._build_sqlserver_query(table_name, page, per_page)
        elif self.database_type == 'X-Inceptor':
            query = self._build_mysql_query(table_name, page, per_page)
        else:
            raise ValueError("Unsupported database type")
        return query.text

    @staticmethod
    def get_count(db_class, response):
        if db_class == "oracle.jdbc.OracleDriver":
            return response["data"][0]["COUNT"]
        elif db_class == "com.mysql.cj.jdbc.Driver":
            return response["data"][0]["count"]
        else:
            return response["data"][0]["count"]

    def build_sql_by_db_class(self, db_class, table_name, page, per_page):
        '''根据数据库类型构建sql'''
        pass


    def _build_oracle_query(self, table_name, page, per_page, oracle_12c_after=True):
        # czj当前固定的oracle版本为11g。
        if not oracle_12c_after:
            offset = (page - 1) * per_page
            query = text(
                f"SELECT * FROM {table_name} OFFSET {offset} ROWS FETCH NEXT {per_page} ROWS ONLY"
            )
            return query
        else:
            query = text(
                f"SELECT * FROM (SELECT A.*, ROWNUM AS rn FROM (SELECT * FROM {table_name}) A) WHERE rn BETWEEN {(page - 1) * per_page + 1} AND {page * per_page}"
            )
            return query

    def _build_mysql_query(self, table_name, page, per_page):
        # query = select().select_from(text(table_name)).offset((page - 1) * per_page).limit(per_page)
        query = text(f"select * from {table_name} limit {(page - 1) * per_page},{per_page}")
        return query

    def _build_postgres_query(self, table_name, page, per_page):
        offset = (page - 1) * per_page
        query = select().select_from(table_name).offset(offset).limit(per_page)
        return query

    def _build_sqlserver_query(self, table_name, page, per_page):
        offset = (page - 1) * per_page
        query = select().select_from(table_name).offset(offset).limit(per_page)
        return query

    @staticmethod
    def get_gt_gsxx_sql(uniscid, entname):
        """
        构建个体工商信息查询sql,
        regno: 注册号
        entname: 个体名称
        """
        field = ["traname","capam","INDUSTRYPHY","INDUSTRYCO","busscoandform","ABUITEMCO","cbuitem","OPELOCDISTRICT",
                 "OPLOC","opfrom","opto","REGORG","localadm","localarea","STATUS","estdate","opername","tel","uniscid",
                 "indate","outdate","revdate","candate"]
        sql_field = ",".join(field)
        if uniscid:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.gt_gsxx} WHERE UNISCID = '{uniscid}' AND ROWNUM=1"
            )
            return query.text
        elif entname:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.gt_gsxx} WHERE TRANAME = '{entname}' AND ROWNUM=1"
            )
            return query.text
        else:
            return False

    @staticmethod
    def get_qy_gsxx_sql(uniscid, entname):
        """
        查询企业工商信息
        uniscid: 统一社会信用代码
        entname: 企业名称
        """
        field = ["entname","entcat","regcap","industryphy","industryco","estdate","regorg","phone","busscope",
                 "localadm","domdistrict","dom","oplocdistrict","frname","tel","indate","outdate","revdate","candate",
                 "UNISCID","entstatus","nsstatus"]
        sql_field = ",".join(field)
        if uniscid:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.qy_gsxx} WHERE UNISCID = '{uniscid}' AND ROWNUM=1"
            )
            return query.text
        elif entname:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.qy_gsxx} WHERE ENTNAME = '{entname}' AND ROWNUM=1"
            )
            return query.text
        else:
            return False

    @staticmethod
    def get_ssxx_sql(uniscid, entname):
        """
        查询税收信息
        uniscid: 统一社会信用代码
        entname: 企业名称
        """
        field = ["CBDWMC","JFRS","NSRSBH","TYSHXYDM"]
        sql_field = ",".join(field)
        if uniscid:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.ssxx} WHERE TYSHXYDM = '{uniscid}' AND ROWNUM=1"
            )
            return query.text
        elif entname:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.ssxx} WHERE CBDWMC = '{entname}' AND ROWNUM=1"
            )
            return query.text
        else:
            return False

    @staticmethod
    def get_qyzl_sql(uniscid, entname):
        """查询企业专利"""
        field = ["zlqr", "sl", "UNISCID"]
        sql_field = ",".join(field)
        if uniscid:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.qyzl} WHERE UNISCID = '{uniscid}' AND ROWNUM=1"
            )
            return query.text
        elif entname:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.qyzl} WHERE ZLQR = '{entname}' AND ROWNUM=1"
            )
            return query.text
        else:
            return False

    @staticmethod
    def get_nsls_sql(uniscid, entname):
        """查询税收信息"""
        field = ["qymc", "bnlj", "bnzf","UNISCID"]
        sql_field = ",".join(field)
        if uniscid:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.nsls} WHERE UNISCID = '{uniscid}' AND ROWNUM=1"
            )
            return query.text
        elif entname:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.nsls} WHERE QYMC = '{entname}' AND ROWNUM=1"
            )
            return query.text
        else:
            return False

    @staticmethod
    def get_ryjy_sql(zjhm:list, start_date: str, end_date:str):
        """查询人员就医信息"""
        field = ["DDYYJGBH", "YXZJHM", "BRJSSJ"]
        query_yy_total = text(
            f"SELECT {field[0]} AS yljgbh, COUNT({field[1]}) AS total FROM {TableMap.ryjy} "
            f"WHERE {field[1]} IN {tuple(zjhm)} AND BRJSSJ BETWEEN TO_DATE('{start_date}', 'YYYY-MM-DD') AND TO_DATE('{end_date}', 'YYYY-MM-DD') GROUP BY {field[0]}"
        )
        query_total = text(
            f"SELECT COUNT(*) AS total FROM {TableMap.ryjy} "
            f"WHERE {field[1]} IN {tuple(zjhm)} AND BRJSSJ BETWEEN TO_DATE('{start_date}', 'YYYY-MM-DD') AND TO_DATE('{end_date}', 'YYYY-MM-DD') "
        )
        query_rc = text(
            f"SELECT COUNT(DISTINCT {field[1]}) AS total FROM {TableMap.ryjy} "
            f"WHERE {field[1]} IN {tuple(zjhm)} AND BRJSSJ BETWEEN TO_DATE('{start_date}', 'YYYY-MM-DD') AND TO_DATE('{end_date}', 'YYYY-MM-DD') "
        )
        return query_total.text, query_yy_total.text, query_rc.text

    @staticmethod
    def get_qyjbxx_sql(uniscid, entname):
        """查询 企业基本信息"""
        field = ["entname", "entcat", "uniscid", "estdate", "industryco", "regcap", "empnum", "regorg", "dom", "lerep", "tel"]
        sql_field = ",".join(field)
        if uniscid:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.qyjbxx} WHERE UNISCID = '{uniscid}' AND ROWNUM=1"
            )
            return query.text
        elif entname:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.qyjbxx} WHERE ENTNAME = '{entname}' AND ROWNUM=1"
            )
            return query.text
        else:
            return False

    @staticmethod
    def get_qyjjyxqk_sql(uniscid, entname):
        """查询 企业经济运行情况"""
        field = ["qymc", "nd", "zyywsr", "nszewy", "qysds", "zzs", "lrze"]
        sql_field = ",".join(field)
        if uniscid:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.qyjjyxqk} WHERE UNISCID = '{uniscid}'"
            )
            return query.text
        elif entname:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.qyjjyxqk} WHERE QYMC = '{entname}'"
            )
            return query.text
        else:
            return False

    @staticmethod
    def get_qyzlxx_sql(uniscid, entname):
        """查询 企业专利信息"""
        field = ["qymc", "zlqr", "zlh", "gbr", "zllx", "zlmc"]
        sql_field = ",".join(field)
        if uniscid:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.qyzlxx} WHERE UNISCID = '{uniscid}'"
            )
            return query.text
        elif entname:
            query = text(
                f"SELECT {sql_field} FROM {TableMap.qyzlxx} WHERE QYMC = '{entname}'"
            )
            return query.text
        else:
            return False

    @staticmethod
    def get_jyxx_sql(qhdm: list, start_date: str, end_date: str):
        """
        查询就医信息.
        响应：
        总人数
        机构信息统计：
            总就医人次

            机构基础信息

        """
        # 总人数

        qhdm = [f"XZQHCODE like '%{item}%'" for item in qhdm]
        qhdm_tuple = " OR ".join(qhdm)
        #
        # 社区总人数
        allTotal = text(
            f"select sum(sl) AS TOTAL from (select xzqhcode,count(sfzh)sl from {TableMap.rkxx} where {qhdm_tuple} group by xzqhcode)"
        )


        # 总就医人数
        allJyrsTotal = text(
            f"SELECT COUNT(DISTINCT(CBRYID)) AS TOTAL FROM {TableMap.jyxx} WHERE {qhdm_tuple} AND BRJSSJ BETWEEN TO_DATE('{start_date}', 'YYYY-MM-DD') AND TO_DATE('{end_date}', 'YYYY-MM-DD')"
        )

        # 总就医人次
        allJyrc = text(
            f"SELECT COUNT(*) AS TOTAL FROM {TableMap.jyxx} WHERE {qhdm_tuple} AND BRJSSJ BETWEEN TO_DATE('{start_date}', 'YYYY-MM-DD') AND TO_DATE('{end_date}', 'YYYY-MM-DD')"
        )
        # 机构排名
        jgpm = text(
            f"SELECT YYQC,JD,WD,SJZJJB,XZ,SFSQYY,XZCODE,JBCODE, COUNT(*) AS JYRC FROM {TableMap.jyxx} "
            f"WHERE {qhdm_tuple} AND BRJSSJ BETWEEN TO_DATE('{start_date}', 'YYYY-MM-DD') AND TO_DATE('{end_date}', 'YYYY-MM-DD') "
            f"GROUP BY YYQC,JD,WD,SJZJJB,XZ,SFSQYY,XZCODE,JBCODE ORDER BY JYRC DESC"

        )

        return allTotal.text,allJyrsTotal.text,allJyrc.text,jgpm.text

    @staticmethod
    def get_qymcbg_sql(uniscid:str):
        """
        企业名称变更
        """
        qymc = text(
            f"select entname AS qymc FROM {TableMap.qymcbg} WHERE uniscid = '{uniscid}' AND ROWNUM=1"
        )
        cym = text(
            f"select OLD_NAME, STARTDATE, ENDDATE FROM {TableMap.qymcbg} WHERE uniscid = '{uniscid}'"
        )
        return qymc.text, cym.text

    @staticmethod
    def get_qygdtzzb_sql(uniscid:str):
        """
        企业股东投资占比
        """
        qymc = text(
            f"select entname AS qymc FROM {TableMap.qygdtzzb} WHERE uniscid = '{uniscid}' AND ROWNUM=1"
        )
        gdxx = text(
            f"select INV AS GDMC, SUBCONPROP AS CGBL FROM {TableMap.qygdtzzb} WHERE uniscid = '{uniscid}'"
        )
        return qymc.text, gdxx.text




if __name__ == '__main__':
    # print(OracleServices().paginate('LING.DEPT',1,5))

        # 创建查询构建器
    # query_builder = QueryBuilder(database_type='oracle')
    #
    # # 设置分页参数
    # table_name = 'LING.DEPT'
    # page_number = 1
    # items_per_page = 10

    # 构建查询语句
    # query = query_builder.build_query(table_name, page_number, items_per_page)
    # aa = QueryBuilder.get_gt_gsxx_sql("123","456")
    #
    #
    # # 输出生成的查询语句
    # print(aa)
    print(QueryBuilder.get_ryjy_sql(['1','2','3'], "2022-11-01", "2022-11-01"))




