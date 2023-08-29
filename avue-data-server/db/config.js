
const MYSQL_HOST = process.env.MYSQL_HOST ? process.env.MYSQL_HOST : '127.0.0.1';
const MYSQL_USER = process.env.MYSQL_USER ? process.env.MYSQL_USER : 'root';
const MYSQL_PWD = process.env.MYSQL_PWD ? process.env.MYSQL_PWD : 'root';
const MYSQL_PORT = process.env.MYSQL_PORT ? process.env.MYSQL_PORT : '3307';
const MYSQL_DB = process.env.MYSQL_DB ? process.env.MYSQL_DB : 'antv_data_server';

export default {
  // 数据库配置
  mysql: {
    host: MYSQL_HOST,
    user: MYSQL_USER,
    password: MYSQL_PWD,
    port: MYSQL_PORT,
    database: MYSQL_DB,
    // host: '10.245.142.253',
    // user: 'root',
    // password: 'root',
    // port: '3306',
    // database: 'antv_data_server',
    useConnectionPooling: true,
    // supportBigNumbers: true,
    // bigNumberStrings: true
  },
  //不配置阿里云oss，默认走本地图片存储
  //阿里云oss作用就是用来存储图片的
  aliyun: {
    accessKeyId: '',
    accessKeySecret: '',
    region: '',
    endpoint: '',
    bucket: '',
  }
}