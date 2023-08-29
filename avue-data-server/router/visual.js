
import resbody from '../util/resbody.js';
import path from 'path'
import fs from 'fs';
import visualDao from '../dao/visual.js';
import config from '../db/config.js';
import { getLoginId } from '../util/utils.js';
import OSS from 'ali-oss';
import multer from 'multer';
import bodyParser from 'body-parser';
const filePath = path.resolve(".") + '/file'
var jsonParser = bodyParser.json({ limit: '1000mb' });
let url = '/visual'
export default (app) => {
  app.get(url + '/list', jsonParser, function (req, res) {
    const query = req.query;
    query.loginid = getLoginId(req);
    visualDao.list(query).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.get(url + '/detail', jsonParser, function (req, res) {
    const id = req.query.id;
    const loginid = getLoginId(req);
    visualDao.detail(id, loginid).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  //图片获取
  app.get('/oss/:name', function (req, res) {
    var name = req.params.name
    if (!name) res.json(resbody.getFailResult('', 'name不能为空'));
    var des_file = filePath + "/" + name;
    fs.exists(des_file, function (flag) {
      if (flag) {
        var data = fs.readFileSync(des_file);
        res.header('Content-Type', 'image/jpg')
        res.end(data);
      } else {
        res.json(resbody.getFailResult('', '图片不存在'));
      }
    })
  })

  app.post(url + '/put-file', jsonParser, multer({ dest: filePath + '/' }).any(), function (req, res) {
    const host = 'http://' + req.headers.host
    const path = filePath + "/"
    const uploadPath = req.files[0].path
    var name = new Date().getTime() + '.jpg';
    var des_file = path + name;
    var data = fs.readFileSync(uploadPath);
    fs.writeFileSync(des_file, data);
    fs.rm(uploadPath, () => { })
    if (config.aliyun.accessKeyId == '') {
      res.json(resbody.getSuccessResult({
        name: name,
        link: '/oss/' + name
      }));
    } else {
      let client = new OSS(config.aliyun)
      client.put(name, des_file).then(data => {
        res.json(resbody.getSuccessResult(Object.assign(data, { link: data.url })));
      }).catch(error => {
        res.json(resbody.getFailResult(error));
      }).finally(() => {
        fs.rm(uploadPath, () => { })
        fs.rm(des_file, () => { })
      });
    }
  })
  app.post(url + '/update', jsonParser, function (req, res) {
    const data = req.body;
    data.loginid = getLoginId(req);
    let name = data.config ? 'update' : 'updates'
    visualDao[name](data).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/copy', jsonParser, function (req, res) {
    const id = req.query.id;
    const loginid = getLoginId(req);
    visualDao.copy(id, loginid).then(data => {
      res.json(resbody.getSuccessResult(data.id));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/save', jsonParser, function (req, res) {
    const data = req.body;
    data.loginid = getLoginId(req);
    visualDao.save(data).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/remove', jsonParser, function (req, res) {
    const ids = req.query.ids;
    const loginid = getLoginId(req);
    visualDao.del(ids, loginid).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })

}
