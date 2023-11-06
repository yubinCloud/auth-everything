export const deepClone = (data) => {
  return JSON.parse(JSON.stringify(data))
}


export const getDsConf = (body = {}) => {
  return {
    driverClass: body.driverClass,
    url: body.url,
    username: body.username,
    password: body.password
  }
}

export const getSqlConfig = (body = {}) => {

  let url = body.url;
  let driverClass = body.driverClass
  let type = (() => {
    if (driverClass == 'com.mysql.cj.jdbc.Driver') {
      return 0
    } else if (driverClass == 'com.microsoft.sqlserver.jdbc.SQLServerDriver') {
      return 1
    }
  })()
  if (type == 0) {
    url = url.split('//')[1]
    url = url.split('?')[0]
    let host = url.split(':')[0]
    let database = url.split('/')[1]
    let port = url.split(':')[1].split('/')[0]
    return {
      host: host,
      user: body.username,
      password: body.password,
      port: port,
      useConnectionPooling: true,
      database: database,
      type: type
    }
  } else if (type == 1) {
    url = url.split('//')[1]
    let host = url.split(':')[0]
    let database = url.split('DatabaseName=')[1]
    let port = url.split(':')[1].split(';')[0]
    return {
      user: body.username,
      password: body.password,
      database: database,
      port: port,
      server: host,
      type: type
    }

  } else {
    return {}
  }
}
export const toColumn = (id, column) => {
  let result = [{
    key: id,
    uuid: true
  }]
  column.forEach(ele => {
    result.push({
      key: ele
    })
  })
  return result;

}

/**
 * 将数据库中的结果按照 column 映射出来
 * @param {*} column 
 * @param {*} data 一个 object，key 是 column 中的字符串元素，value 是数据库中的 cell 值
 * @returns 返回一个 object，keys 是 column 中的所有元素
 */
export const hTol = (column, data) => {
  let result = {}
  column.forEach(ele => {
    result[ele] = data[toHump(ele)]
  })
  return result;
}

// 转换变量的风格，toHump("yu_bin") == "yuBin"
export const toHump = (name) => {
  return name.replace(/\_(\w)/g, function (all, letter) {
    return letter.toUpperCase();
  });
}
export const toLine = (name) => {
  return name.replace(/([A-Z])/g, "_$1").toLowerCase();
}
export const getModuleParams = (column = [], data) => {
  let result = {};
  column.forEach(ele => {
    let value = data[ele]
    if (value !== undefined && value !== null && value !== 'null') {
      result[ele] = data[ele]
    }
  })
  return result
}
export const getClientIP = (req) => {
  return req.headers['x-forwarded-for'] || // 判断是否有反向代理 IP
    req.connection.remoteAddress || // 判断 connection 的远程 IP
    req.socket.remoteAddress || // 判断后端的 socket 的 IP
    req.connection.socket.remoteAddress;
};
export const validatenull = (val) => {
  // 特殊判断
  if (val && parseInt(val) === 0) return false;
  const list = ['$parent'];
  if (typeof val === 'boolean') {
    return false;
  }
  if (typeof val === 'number') {
    return false;
  }
  if (val instanceof Array) {
    if (val.length === 0) return true;
  } else if (val instanceof Object) {
    val = deepClone(val);
    list.forEach(ele => {
      delete val[ele];
    });
    if (JSON.stringify(val) === '{}') return true;
  } else {
    if (
      val === 'null' ||
      val == null ||
      val === 'undefined' ||
      val === undefined ||
      val === ''
    ) {
      return true;
    }
    return false;
  }
  return false;
}

export const getLoginId = (req) => {
  return req.get("User");
}