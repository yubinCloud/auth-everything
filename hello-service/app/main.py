from fastapi import FastAPI, Header, Request
from typing import Annotated, Union

app = FastAPI()


@app.get("/")
def read_root(name: str = 'World'):
    return {"Hello": name}


@app.get('/admin')
def only_admin():
    return "Only admin can see it."


@app.get('/whoami')
def whoami(
    user: Annotated[Union[str, None], Header(...)] = None
):
    return f"I am {user}"


@app.get('/header')
def header(
    request: Request
):
    print(request.headers)
    return {
        'jupyter_token': request.headers.get("authorization")
    }


@app.post('/headers')
def headers(
    request: Request
):
    print(request.headers)
    return {
        "yes": "yes"
    }


@app.post('/api/nl_sql')
def nl2sql(request: Request):
    print(request)
    return {
        'code': 200,
        'msg': '',
        'data': {
            'sql': "SELECT * FROM mock",
            'chart': 'bar',
            'guess': [
                '今年的销售收入较去年相比增长了多少？',
                '有哪些产品或服务贡献了今年销售收入的主要增长？'
            ]
        }
    }