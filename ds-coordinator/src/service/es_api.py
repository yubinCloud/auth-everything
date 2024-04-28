'''
针对es数据的接口
'''
from elasticsearch_dsl import Search, Q, Nested
from elasticsearch_dsl.connections import connections
from config import settings
from database.es.dataSourceModel import TableInfo,FieldInfo
from elasticsearch import Elasticsearch


class EsDbSave:
    def __init__(self):
        connections.create_connection(hosts=[f'{settings["es"]["host"]}:{settings["es"]["port"]}'], timeout=20)

    def store_data(self,data, ignore_check=False):
        obj = self.format_document(data)
        return self.get_or_create(obj, ignore_check)

    def get_or_create(self, obj, ignore_check=False):
        """判断是否已经存在"""
        if not ignore_check:
            query = self.get_query(obj)
            query = obj.search().query("bool", **query)
            if query.count() == 0:
                return self.save(obj, obj.table_name)
            else:
                print(f"{obj.table_name}已存在")
                return False
        else:
            return self.save(obj, obj.table_name)

    def save(self, obj, key_name):
        try:
            obj.save()
            print(f"插入{key_name}成功")
            return True
        except Exception as e:
            print(e)
            print(f"《ip》插入{key_name}失败")
            return False

    def format_document(self, data):
        obj = TableInfo()
        obj.table_name = data["table_name"]
        obj.conn_name = data["conn_name"]
        obj.owner = data["owner"]
        obj.conn_id = data["conn_id"]
        obj.field_list = []
        for item in data["field_list"]:
            field = FieldInfo()
            field.field_name = item['COLUMN_NAME']
            field.field_type = item['TYPE_NAME']
            field.field_length = item['COLUMN_SIZE']
            field.field_remark = item["REMARKS"]
            field.field_default = item["COLUMN_DEF"]
            field.field_is_null = item["IS_NULLABLE"]
            obj.field_list.append(field)
        return obj

    def get_query(self,obj):
        query = {
            "must": [
            ]
        }
        if obj.table_name:
            query["must"].append({"match": {"table_name": obj.table_name}})
        if obj.conn_id:
            query["must"].append({"match": {"conn_id": obj.conn_id}})
        return query


class EsDbSearch():
    def __init__(self):
        connections.create_connection(hosts=[f'{settings["es"]["host"]}:{settings["es"]["port"]}'], timeout=5)

    def get_page(self, page, page_size):
        # 处理分页,es
        start = (page - 1) * page_size
        return {"from_": start, "size": page_size}

    def kw_search(self, keyword, page, page_size, headers:dict):
        table = TableInfo()
        s = table.search().query("bool", must=[
            {"match": {"owner": headers['user']}},
            {"nested": {
                "path": "field_list",
                "query": {"wildcard": {"field_list.field_name": f"*{keyword}*"}}
            }}
        ])
        # TODO：检查缓存，如果有则直接返回
        hits = self.get_response(s)
        # 提取符合要求的元素
        filtered_elements = self.get_result(hits, keyword)
        return filtered_elements[(page-1)*page_size:page*page_size], len(filtered_elements)

    def get_fields(self, conn_id, table_name:str, user:str):
        "从es中获取所有的field，根据conn_id,table_name"
        timer = 3
        while True:
            if timer > 3:
                print("# 超时请求3次，放弃请求")
                return []
            try:
                table = TableInfo()
                s = table.search().query(
                    "bool",
                    must=[
                        {"match": {"conn_id": conn_id}},
                        {"match": {"table_name": table_name}},
                        {"match": {"owner": user}}
                    ],
                ).params(request_timeout=3)
                result = s.execute()
                if result.hits:
                    result = [hit["_source"] for hit in result["hits"]["hits"]][0]
                    return result.to_dict()
                else:
                    return None
            except Exception as e:
                print(e)
                timer += 1
                continue

    def get_response(self, s):
        hits = []
        for i in range(3):
            try:
                result = s.execute()
                hits = result.hits.hits
                break
            except Exception as e:
                print("es_search:", e)
                continue
        return hits

    def get_result(self, hits, keyword):
        "从hits中提取符合要求的数据，并格式化"
        filtered_elements = []

        for hit in hits:
            for field in hit._source.field_list:
                field = field.to_dict()
                if keyword in field["field_name"]:
                    entry = {
                        "data_source": hit._source.conn_name,
                        "data_source_id": hit._source.conn_id,
                        "table_name": hit._source.table_name,
                        "field_name": field.get("field_name", None),
                        "field_type": field.get("field_type", None),
                        "field_length": field.get("field_length", None),
                        "field_remark": field.get("field_remark", None),
                        "field_default": field.get("field_default", None),
                    }
                    filtered_elements.append(entry)
        return filtered_elements


class ApiEsDbSearch():
    def __init__(self):
        self.es = Elasticsearch([{'host': '10.245.142.253', 'port': 9200}])
        # connections.create_connection(hosts=[f'{settings["es"]["host"]}:{settings["es"]["port"]}'], timeout=5)

    def search(self):
        query = {
            "bool": {
                "must": [
                    {
                        "term": {
                            "owner": "user"
                        }
                    },
                    {
                        "nested": {
                            "path": "field_list",
                            "query": {
                                "wildcard": {
                                    "field_list.field_name": "*D*"
                                }
                            }
                        }
                    }
                ]
            }
        }
        result = self.es.search(index="aet-datasource", query=query)
        for hit in result['hits']['hits']:
            print(hit['_source'])




if __name__ == '__main__':
    # es_ = EsDbSearch()

    # # 搜索es数据库中field_name=age的数据
    # s = Search().query("match_phrase", field_list.field_name="test")
    # response = s.execute()
    # result = [hit["_source"] for hit in response["hits"]["hits"]]
    # print(result)
    # print(len(es_.kw_search("admin",2,5)))


    # api
    es = ApiEsDbSearch()
    es.search()






