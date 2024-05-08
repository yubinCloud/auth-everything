import copy
import json

import requests
from config import settings
from service.aes_encrypt import encrypt_aes



class AvueApi():
    def __init__(self):
        self.url = "/db"

    def get_url(self, uri):
        return f"http://{settings.avue_host}{uri}"

    def base_request(self, method, url, payload, headers=None):
        '''基本的request请求，所有请求都从这里发出'''
        default_headers = {
            'Content-Type': 'application/json',
        }
        if headers:
            default_headers.update(headers)

        print("# avue url:", url)
        timer = 3

        error_list = []
        for i in range(timer):
            try:
                res = None
                if method == "get":
                    res = requests.request(method, url, headers=default_headers, params=payload, timeout=2)
                elif method == "post":
                    res = requests.request(method, url, headers=default_headers, json=payload, timeout=2)
                # 根据code处理
                if res.status_code == 200:
                    return res.json()
                else:
                    print("# avue response status code not is 200: ", res.status_code)
                    return {"code": -1, "msg": f"请联系开发人员检查avue服务"}
            except Exception as e:
                print("# avue error: ", str(e))
                error_list.append(str(e))
        print("# avue error: ", error_list)
        return {"code": -1, "msg": f"请联系开发人员检查avue服务"}


    def format_response(self, response):
        '''格式化返回值'''
        print("avue response: ", response)
        if "success" in response.keys():
            del response["success"]
        try:
            if response["code"] == 200:
                response.update({
                    "code": 0,
                    "msg": "请求成功(avue)"
                })
            else:
                response.update({
                    "code": -1,
                    "msg": "请求失败(avue)"
                })
            return response
        except Exception as e:
            return {"code": -1, "msg": str(e)}

    def db_list(self,current,size, headers:dict):
        uri = f"/db/list?current={current}&size={size}"
        url = self.get_url(uri)
        response = self.base_request("get", url, {}, headers=headers)
        return self.format_response(response)

    def db_detail(self, id, headers:dict) ->dict:
        uri = f"/db/detail?id={id}"
        url = self.get_url(uri)
        response = self.base_request("get", url, {}, headers=headers)
        return self.format_response(response)

    def db_remove(self, headers, id):
        uri = f"/db/remove?ids={id}"
        url = self.get_url(uri)
        response = self.base_request("post", url, {}, headers=headers)
        return self.format_response(response)

    def db_test(self, data):
        uri = "/db/db-test"
        url = self.get_url(uri)
        context = encrypt_aes(json.dumps(data))
        response = self.base_request("post", url, headers={"data": context}, payload={})
        return self.format_response(response)

    def db_dynamic_query(self, sql, loginId, id):
        uri = "/dynamic-query"
        url = self.get_url(uri)
        response = self.base_request("post", url, {"sql": sql, "loginId": loginId, "id": id})
        return response

    def db_save(self, id, data:dict, headers:dict):
        if id:
            data.update({"id":str(id)})
        uri = f"/db/submit"
        url = self.get_url(uri)
        data_ = copy.deepcopy(data)
        for k,v in data_.items():
            if not v:
                del data[k]
        response = self.base_request("post", url, data, headers=headers)
        return self.format_response(response)

    def db_sql_export(self, id, loginid, sql, data):
        uri = f"/sql-export/{id}/{loginid}"
        url = self.get_url(uri)
        data["id"] = id
        data["sql"] = sql


# =========================


# Example usage



if __name__ == '__main__':
    api = AvueApi()
    body = {
        'url': 'jdbc:mysql://10.245.142.253:3307/db0',
        'driverClass': 'com.mysql.cj.jdbc.Driver',
        'username': 'root',
        'password': 'root'
    }

    response = api.db_save(None, body, {"user":"admin"})
    print(response)


    # config = Utils().get_sql_config(body)
    # print(config)




