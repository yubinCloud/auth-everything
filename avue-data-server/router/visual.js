import resbody from '../util/resbody.js';
import path from 'path'
import fs from 'fs';
import visualDao from '../dao/visual.js';
import config from '../db/config.js';
import { getLoginId } from '../util/utils.js';
import OSS from 'ali-oss';
import multer from 'multer';
import bodyParser from 'body-parser';
import { getUserInfo, addVisualForUser, addVisualForRole } from '../exchange/ssoauth.js';
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

  app.get(url + '/test-put', function (req, res) {
    const path = filePath + "/";
    console.log(path);
    res.json(resbody.getSuccessResult({
      p: path
    }))
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
  /**
   * 增加大屏时，要通过请求 sso-auth 来为该用户添加 permission
   */
  app.post(url + '/save', jsonParser, function (req, res) {
    const data = req.body;
    const loginid = getLoginId(req);
    data.loginid = loginid;
    visualDao.save(data).then(data => {
      visualId = data.id;
      addVisualForUser(loginid, visualId).then(resp => {
        res.json(resbody.getSuccessResult(data));
      }).catch(error => {
        visualDao.del(visualId, loginid).then(d => {
          res.json(resbody.getFailResult(error));
        }).catch(e => {
          res.json(resbody.getFailResult(error));
        })
      })
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
  
  /**
   * 自己添加的 API
   */
  app.get(url + '/preview-list', jsonParser, function (req, res) {
    const loginid = getLoginId(req);
    getUserInfo(loginid).then(axiosResp => {
      const userInfo = axiosResp.data;
      const permissionList = userInfo['permissionList'];
      let visualIdList = [];
      for (let i = 0; i < permissionList.length; i++) {
        const perm = permissionList[i];
        if (perm.startsWith("avue:") && perm.length > 5) {
          visualIdList.push(perm.substring(5));
        }
      }
      visualIdList = Array.from(visualIdList)
      const query = {
        id: visualIdList
      }
      visualDao.list(query).then(data => {
        res.json(resbody.getSuccessResult(data));
      }).catch(error => {
        res.json(resbody.getFailResult(error));
      });
    })
  })

  app.post(url + '/assign-permission/user', jsonParser, function (req, res) {
    const loginid = getLoginId(req);
    const visualId = req.body.visualId
    const users = req.body.users;
    const query = {
      loginid: loginid,
      id: visualId
    }
    visualDao.list(query).then(resultSet => {
      if (resultSet.length <= 0) {
        res.json(resbody.getFailResult('不允许分配该视图 ID 的权限'))
      } else {
        // TODO: 为每个用户添加权限
        for (let i = 0; i < users.length; i++) {
          addVisualForUser(users[i], visualId)
        }
      }
    })
  })

}
