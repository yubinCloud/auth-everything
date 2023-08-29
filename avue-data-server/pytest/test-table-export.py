import requests

headers = {
    'Accept': 'application/json, text/plain, */*',
    'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6',
    'Cache-Control': 'no-cache',
    'Connection': 'keep-alive',
    'Content-Type': 'application/json',
    'Pragma': 'no-cache',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.57',
    'data': 'hdvm14NADWoKYo9wW00zI7FaAWaypaE1KaPn6Q5eWWdV7BKwFZGSV9CNoCdU4eVF7aYGA2Y0f4r2UB3XwXoCUg==',
    'Authorization': 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpblR5cGUiOiJsb2dpbiIsImxvZ2luSWQiOiJ5dWJpbiIsInJuU3RyIjoidjlQMUJidERGTVhia01kRktrZG0xTVVxYlY1cEJGenUiLCJyb2xlIjoic3VwZXItYWRtaW4ifQ.ZbZAWO3OTQqnk1SQTl0xnJzeo-dIwkAqJk1Y7YiLU8w',
}

data = 'hdvm14NADWoKYo9wW00zI7FaAWaypaE1KaPn6Q5eWWdV7BKwFZGSV9CNoCdU4eVF7aYGA2Y0f4r2UB3XwXoCUg=='

response = requests.post('http://127.0.0.1:10010/antv/db/sql-export', headers=headers, data=data, verify=False)
print(response.json())