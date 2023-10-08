local headers = ngx.req.get_headers()
local token = headers["Authz"]
-- 尝试从 query params 中获取
if not token then
    local args = ngx.req.get_uri_args()
    token = args.token
end
-- 尝试从 Cookie 中获取
if not token then
    local cookies = ngx.req.get_headers()["Cookie"]
    token = string.match(cookies, "authz=([^;]+)")
end
-- 将 token 转为 jupyter token
local http = require("resty.http")
local httpc = http.new()
local url = "http://sso-auth/auth/internal/user/jupyter/ctxByToken?token="..token
local res, err = httpc:request_uri(url, {
    method = "GET",
    headers = {
        ["Content-Type"] = "application/json"
    }
})
ngx.status = res.status
if ngx.status ~= 200 then
    ngx.exit(402)
end
local jupyterToken = res.body
ngx.req.set_uri_args({ token = jupyterToken })
