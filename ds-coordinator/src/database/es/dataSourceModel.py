from elasticsearch_dsl import Keyword, InnerDoc, Nested, Document
from elasticsearch_dsl.connections import connections

class FieldInfo(InnerDoc):
    field_name = Keyword()                                     # 字段名
    field_type = Keyword()                                     # 字段类型
    field_length = Keyword()                                   # 字段长度
    field_remark = Keyword()                                   # 字段备注
    field_default = Keyword()                                  # 字段默认值
    field_is_null = Keyword()                                  # 字段是否为空


class TableInfo(Document):
    '''表信息'''
    table_name = Keyword()                                     # 表名
    conn_id = Keyword()                                        # 连接ID
    conn_name = Keyword()                                      # 连接名
    owner = Keyword()                                          # 所属用户
    field_list = Nested(FieldInfo,multi=True)                  # 字段列表

    class Index:
        name = 'aet-datasource'
        settings = {
            "number_of_shards": 2,
            "number_of_replicas": 1
        }


if __name__ == '__main__':
    # 创建索引
    import os
    close_es = os.environ.get("CLOSE_ES","false")
    if close_es.upper() == "TRUE":
        print(f"关闭ES索引创建.(不使用es。环境变量设定值为：{close_es})")
    else:
        print(f"开始创建ES索引.(环境变量设定值为：{close_es})")
        for i in range(2):
            try:
                connections.create_connection(hosts=[f'aet-elasticsearch7:9200'], timeout=50)
                TableInfo.init()
                print('创建Es索引成功')
                break
            except Exception as e:
                print(e)
                continue

