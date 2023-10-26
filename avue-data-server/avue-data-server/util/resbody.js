const SUCCESS = 200,
  FAIL = 400,
  ERROR = 500,
  NOTFIND = 404,
  AUTHOR = 403;
export default {
  config: {
    code: null,
    msg: null,
    data: null,
    success: null
  },
  init: function (data, msg) {
    (data) ? this.config.data = data : this.config.data = null;
    if (msg) this.config.msg = msg;
  },
  getSuccessResult: function (data, msg) {
    this.config.code = SUCCESS;
    this.config.msg = '请求成功';
    this.config.success = true
    this.init(data, msg);
    return this.config;
  },
  getFailResult: function (data = '', msg) {
    this.config.code = FAIL;
    this.config.msg = '请求失败';
    this.config.success = false
    this.init(data.toString(), msg);
    return this.config;
  }

}
