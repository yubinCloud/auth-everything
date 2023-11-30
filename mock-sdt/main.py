import uvicorn
from fastapi import FastAPI, Query
from fastapi.middleware.cors import CORSMiddleware


app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=['*'],
    allow_credentials=False,
    allow_methods=["*"]
)

@app.get('/cgi-bin/gettoken')
def get_access_token(
    corpid: str = Query(...),
    corpsecret: str = Query(...)
):
    if corpid == 'wwafa1a3005a15a672' and corpsecret == 'GYdb6LBh-FR3Eu-1bhmLXw_YavBdqUXwce5ooSz_hFc':
        return {
            'errcode': 0,
            'errmsg': 'ok',
            'access_token': 'tat',
            'expires_in': 20
        }
    return {
        'errcode': -1,
        'errmsg': 'ok',
        'access_token': 'tat',
        'expires_in': 20
    }


@app.get('/cgi-bin/user/getuserinfo')
def get_userinfo(
    access_token: str = Query(...),
    code: str = Query(...)
):
    if access_token == 'tat' and len(code) > 0:
        return {
            'errcode': 0,
            'errmsg': 'ok',
            'UserId': '121'
        }
    return {
        'errcode': -1,
        'errmsg': 'fail',
        'UserId': ''
    }


@app.get('/cgi-bin/user/get')
def get_user_detail(
    access_token: str = Query(...),
    userid: str = Query(...),
    avatar_addr: int = Query(...)
):
    if access_token == 'tat' and userid == '121':
        return {
            'errcode': 0,
            'errmsg': 'ok',
            'userid': '121',
            'name': 'admin',
            'mobile': '17863116898',
            'hide_mobile': 0
        }
    return {
        'errcode': -1,
        'errmsg': 'fail',
        'UserId': ''
    }


if __name__ == '__main__':
    uvicorn.run(app, host='127.0.0.1', port=12100)