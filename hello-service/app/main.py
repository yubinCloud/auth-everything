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
    return ""
