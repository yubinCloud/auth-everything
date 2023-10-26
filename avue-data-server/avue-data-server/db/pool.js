
import mysql from 'mysql';
import mssql from 'mssql';
import config from './config.js';
import log4js from 'log4js';
import log4Config from './log4j.js'
log4js.configure(log4Config);
import moment from 'moment'
var logger = log4js.getLogger('mysql');
import { validatenull, deepClone, toHump } from '../util/utils.js'
let pool = {};
function detailJSON (str = '') {
  str = str.replace(/'/g, "\\'").replace(/\\n/g, "\\\\n").replace(/\\t/g, "\\\\t")
  return str
}
export const content = (c, s) => {
  const type = c.type
  return new Promise((resolve, reject) => {
    let p
    if (type == 0) {
      p = mysql.createConnection(c);
      p.connect(function (err) {
        if (err) reject();
        else {
          if (s) {
            p.query(s, [], function (err, result) {
              p.destroy()
              resolve(result);
            }, (error) => {
              resolve(error);
            });
          } else {
            resolve();
          }
        }
      });
    } else if (type == 1) {
      mssql.connect(c).then(sql => {
        if (s) {
          p = sql.query(s).then(result => {
            resolve(result);
          }).catch((error) => {
            resolve(error);
          });
        } else {
          resolve();
        }
      })
    }
  })
}
function start () {
  return new Promise((resolve, reject) => {
    let result = mysql.createConnection(config.mysql);
    result.connect(function (err) {
      if (err) {
        reject(err);
      } else {
        resolve(result);
      }

    });
  })

}
export const connection = start;

function getUpdateSql (params) {
  let list = [];
  Object.keys(params).forEach(ele => {
    let dataValue = params[ele]
    if (typeof (dataValue) === 'number') {
      list.push(`${ele} = ${dataValue}`)
    } else {
      list.push(`${ele} = '${detailJSON(dataValue)}'`)
    }

  })
  return ` SET ${list.join(',')}`

}
function getParamsSql (params) {
  let list = [];
  Object.keys(params).forEach(ele => {
    let value = params[ele];
    if (!Array.isArray(value)) {
      value = value + '';
    }
    if (ele === 'loginid') {
      list.push(`loginid = '${value}'`)
      return
    }
    if (validatenull(value) || value.indexOf('%%') != -1) return
    if (value.indexOf('%') != -1) {
      list.push(`${ele} LIKE '${value}'`)
    } else if (Array.isArray(value)) {
      list.push(`${ele} IN (${(() => value.map(item => {
        return `'${item}'`
      }))()})`)
    } else {
      let dataValue = value
      if (typeof (dataValue) === 'number') {
        list.push(`${ele} = ${dataValue}`)
      } else {
        list.push(`${ele} = '${detailJSON(dataValue)}'`)
      }
    }
  })
  return !validatenull(list) ? ` WHERE ${list.join(' AND ')}` : ''
}

function getPageSql ({ size, page }) {
  let sql = ` LIMIT ${(page - 1) * size},${size}`
  return sql;
}

export const insert = ({ table, column, data }, fn) => {
  let sql = `INSERT INTO ${table}(${column.map(ele => ele.key)}) VALUES`
  let column_sql = [];
  column.forEach(item => {
    if (item.uuid) {
      let uuid = Number(new Date().getTime() + parseInt(Math.random() * 10000))
      if (fn) fn(uuid)
      column_sql.push(uuid)
    } else {
      if (data[item.key] instanceof Date) {
        data[item.key] = moment(data[item.key]).format('YYYY-MM-DD HH:mm:ss');
      }
      let dataValue = data[item.key]
      if (typeof (dataValue) === 'number') {
        column_sql.push(`${data[item.key]}`)
      } else[
        column_sql.push(`'${detailJSON(data[item.key])}'`)
      ]

    }
  })
  sql = `${sql}(${column_sql.join(',')})`;
  return query(sql)
}
export const update = ({ table, data, params }) => {
  let sql = `UPDATE ${table}`;
  let valid = {
    update: !validatenull(params),
    params: !validatenull(data)
  }
  if (valid.update) {
    sql = `${sql}${getUpdateSql(params)} `
  }
  if (valid.params) {
    sql = `${sql}${getParamsSql(data)} `
  } else {
    return ''
  }
  return query(sql)
}
export const del = ({ table, data }) => {
  let params = data;
  let sql = `DELETE FROM ${table}`;
  let valid = {
    params: !validatenull(params)
  }
  if (valid.params) {
    sql = `${sql}${getParamsSql(params)} `
  } else {
    return ''
  }
  return query(sql)
}

export const insetList = ({ table, column, data }) => {
  let sql = `INSERT INTO ${table}(${column.map(ele => ele.key)}) VALUES`
  let column_sql = [];
  data.forEach(ele => {
    let list = [];
    column.forEach(item => {
      if (item.uuid) {
        list.push('uuid()')
      } else {
        list.push(`'${ele[item.key] || ''}'`)
      }
    })
    column_sql.push(`(${list.join(',')})`)
  })
  sql = sql + column_sql.join(',');
  return query(sql)
}
export const list = ({ table, data, parent, hump, column }, order) => {
  return new Promise((resolve) => {
    let result = {
      total: 0,
      records: []
    }
    console.log(data.id)
    console.log(Array.isArray(data.id))
    let params = deepClone(data);
    console.log(Array.isArray(params.id))
    let sql = `SELECT ${column ? column : '*'} FROM ${table}`;
    let page = data.current;
    let size = data.size;
    delete params.current;
    delete params.size;
    let valid = {
      params: !validatenull(params),
      page: !validatenull(page) && !validatenull(size),
      order: !validatenull(order)
    }
    if (valid.params) {
      sql = `${sql}${getParamsSql(params)} `
    }
    if (valid.order) {
      sql = `${sql} Order By ${order.key} ${order.order || 'DESC'} `
    }
    if (valid.page) {
      let page_sql = getPageSql({
        page: Number(page),
        size: Number(size)
      })
      sql = `${sql}${valid.params ? '' : ' '}${page_sql}`
    }
    params = Object.keys(params).map(ele => {
      return params[ele]
    })
    let count_sql = sql.replace('*', 'count(*) as count');
    let index = count_sql.indexOf('LIMIT');
    if (index != -1) count_sql = count_sql.substr(0, index)
    query(sql, params).then((res = []) => {
      let records = []
      if (hump) {
        res.forEach(ele => {
          let item = {}
          Object.keys(ele).forEach(obj => {
            item[toHump(obj)] = ele[obj]
          })
          records.push(item)
        })
      } else {
        records = res;
      }
      result.records = records
      return query(count_sql, params)
    }).then((res = []) => {
      result.total = res[0] ? Number(res[0].count) : 0
      if (parent) {
        resolve(result.records)
      } else {
        resolve(result)
      }

    })
  })
}
let query = (sql, params = []) => {
  logger.info('sql===' + sql + '===');
  logger.info('params===' + params.join(',') + '===')
  return new Promise((resolve, reject) => {
    pool.query(sql, params, function (err, result) {
      resolve(result);
    }, (error) => {
      reject(error);
    });
  })
}
export default query

start().then(result => {
  logger.info("数据库连接成功");
  pool = result;
  setInterval(() => {
    pool.query('use `' + config.mysql.database + '`')
  }, 120 * 1000);
}).catch(err => {
  logger.error("数据库连接失败" + err)
});
