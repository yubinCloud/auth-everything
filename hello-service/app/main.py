from fastapi import FastAPI

app = FastAPI()


@app.get("/")
def read_root(name: str = 'World'):
    return {"Hello": name}


@app.get('/admin')
def only_admin():
    return "Only admin can see it."