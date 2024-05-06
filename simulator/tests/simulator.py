import requests

requests.post("http://localhost:9660/simulate/save-visual-imgs-sync", json={
    'credits': """{"username":"admin","screenName":"admin","roleList":["super-admin"],"permissionList":["i:nacos","avue:vs:10","avue:84","avue:88"],"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpblR5cGUiOiJsb2dpbiIsImxvZ2luSWQiOiJhZG1pbiIsInJuU3RyIjoiVjZpVmFHR1JxNHdwaG1rdTRwUmVlRFlUWG52cVgyWlgifQ.Vcd2n-b0ULFdDElrpMLcDPHveMyUb__xUZl5U4D-MBY","note":"初始化的管理员","createTime":1691140213}""",
    'visual': {
        'visual_id': '1714119850811',
        'components': ['1fc8f943-ce2e-4ab8-9ad5-66f47030a0f4']
    }
})