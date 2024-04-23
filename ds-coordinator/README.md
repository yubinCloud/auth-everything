# databse-visaliaztion

### 分支说明：
1. master：主分支，用于发布
2. volume: 新版挂在代码目录的分支
3. czj：财政局内的部署版本（挂载了代码目录）。
4. czj-mt：财政局多租户版本

### 上线部署：
1. 要求当前容器启动在es后，方便执行初始化index的操作
2. 

### 客户侧运维：
1. ds-worker的地址：IP:10010/ds-worker/doc.html#
2. oracle数据库版本：所有的均小于12（11g）
3. 依赖环境的导出（linux）：
   ```text
    pip download -d ./ -r requirements.txt
    ```
4. 客户侧docker子模块的更新说明：
     ```text
     1. 代码更新
     2. 代码文件传入aet-ds-worker
     3. 重启容器 
     
    ```
5. 如何切换测试容器：
   ```text
   1. 需要保证docker-compose.yml文件中的
   ```

### 系统内部接口的请求分布
1. 创建/添加sql连接：avue

2. 测试连接：avue测试mysql数据源，db-worker测试非mysql数据源

3. 获取数据源列表 ：avue

4. 获取sql 连接详情 ：avue

5. 更新sql连接：avue

6. 删除sql连接：avue

7. 获取 数据源 的数据库表名列表： avue获取链接详情，db-worker获取表名（固定接口，非执行sql）

8. 获取表详情（字段名）： avue获取链接详情，db-worker处理表（固定接口，非执行sql）

9. 获取sql表的数据： db-worker执行sql

### 接口文件分布：
1. sql_controller: 系统内部接口
2. api_controller: 外部系统接口

### 参考连接：
1. sql的异步查询参考：
 
https://stackoverflow.com/questions/69490450/objectnotexecutableerror-when-executing-any-sql-query-using-asyncengine

### 内网部署(k8s)：
```text
改完之后，把代码上传到 10.245.153.197 的 /root/code/auth-everything-fb-dev/ds-coordinator 目录下

然后运行：kubectl delete deploy ds-coordinator -n aet

然后运行： sh k3s.sh
```


