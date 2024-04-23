import json

import requests
from config import settings
from service.aes_encrypt import encrypt_aes



class AvueApi():
    def __init__(self):
        self.url = "/db"

    def get_url(self, uri):
        return f"http://{settings.avue_host}{uri}"

    def base_request(self, method, url, payload, headers=None, user=''):
        '''基本的request请求，所有请求都从这里发出'''
        if not headers:
            headers = {
                'Content-Type': 'application/json',
                "User": user,
            }
        print("# avue url:", url)
        timer = 3

        error_list = []
        for i in range(timer):
            try:
                res = None
                if method == "get":
                    res = requests.request(method, url, headers=headers, params=payload, timeout=2)
                elif method == "post":
                    res = requests.request(method, url, headers=headers, json=payload, timeout=2)
                # 根据code处理
                if res.status_code == 200:
                    return res.json()
                else:
                    print(res.text)
                    return {"code": -1, "msg": f"请联系开发人员检查avue服务"}
            except Exception as e:
                print("# avue error: ", str(e))
                error_list.append(str(e))
        print("# avue error: ", error_list)
        return {"code": -1, "msg": f"请联系开发人员检查avue服务"}


    def format_response(self, response):
        '''格式化返回值'''
        print("avue: ", response)
        if "success" in response.keys():
            del response["success"]
        try:
            response["code"] = 0 if response["code"] == 200 else -1
            return response
        except Exception as e:
            return {"code": -1, "msg": str(e)}

    def db_list(self,current,size, user:str):
        uri = f"/db/list?current={current}&size={size}"
        url = self.get_url(uri)
        response = self.base_request("get", url, {}, user=user)
        return self.format_response(response)

    def db_detail(self, id, user:str) ->dict:
        uri = f"/db/detail?id={id}"
        url = self.get_url(uri)
        response = self.base_request("get", url, {}, user=user)
        return self.format_response(response)

    def db_remove(self, user, id):
        uri = f"/db/remove?ids={id}"
        url = self.get_url(uri)
        response = self.base_request("post", url, {}, user=user)
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

    def db_save(self, id, data:dict, user:str):
        if id:
            data.update({"id":str(id)})
        uri = f"/db/submit"
        url = self.get_url(uri)
        remove_k = []
        for k,v in data.items():
            if not v:
                remove_k.append(k)
        for k in remove_k:
            del data[k]
        response = self.base_request("post", url, data, user=user)
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

    response = api.db_list(1,999, "admin")
    print(response)


    # config = Utils().get_sql_config(body)
    # print(config)




