import resbody from '../util/resbody.js';
import path from 'path'
import fs from 'fs';
import visualDao from '../dao/visual.js';
import config from '../db/config.js';
import OSS from 'ali-oss';
import multer from 'multer';
import bodyParser from 'body-parser';
import euserSSOExchange from '../exchange/eusersso.js';
const filePath = path.resolve(".") + '/file'
var jsonParser = bodyParser.json({ limit: '1000mb' });
let url = '/visual'
export default (app) => {
  app.get(url + '/list', jsonParser, function (req, res) {
    const query = {
      category: req.query.category,
      current: req.query.current,
      size: req.query.size
    };
    const order = {
      key: 'id',
    };
    visualDao.descList(query, order).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  // 查看大屏的详情
  // 不校验 loginid，权限的校验放在了 gateway 中
  // app.get(url + '/detail', jsonParser, function (req, res) {
  //   const id = req.query.id;
  //   visualDao.detail(id).then(data => {
  //     res.json(resbody.getSuccessResult(data));
  //   }).catch(error => {
  //     res.json(resbody.getFailResult(error));
  //   });
  // })
  app.get(url + '/detail', jsonParser, resbody.asyncHandler(async (req, res, next) => {
    const id = req.query.id;
    const visualDetail = await visualDao.detail(id);
    const euser = req.get("X-Euser");
    if (euser === undefined) {
      res.json(resbody.getSuccessResult(visualDetail));
      return
    }
    const rawComponents = visualDetail.config.component;
    const objComponents = JSON.parse(rawComponents);
    const avuePermissions = await euserSSOExchange.getPersonalAvuePermission(euser);
    /** 找到该大屏的权限信息 */
    let perm;
    for (let i = 0; i < avuePermissions.length; i++) {
      if (avuePermissions[i].visualId == id) {
        perm = avuePermissions[i];
      }
    }
    /** 根据权限信息，过滤出可返回的组件信息并返回 */
    if (perm === undefined) {
      visualDetail.config.component = "";
      res.json(resbody.getSuccessResult(visualDetail));
      return
    }
    const filteratedComponents = [];
    const whitelist = new Set(perm.whitelist);
    for (let i = 0; i < objComponents.length; i++) {
      let component = objComponents[i];
      if (whitelist.has(component['index'])) {
        filteratedComponents.push(component)
      }
    }
    visualDetail.config.component = JSON.stringify(filteratedComponents);
    res.json(resbody.getSuccessResult(visualDetail));
  }));
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
    let name = data.config ? 'update' : 'updates'
    visualDao[name](data).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/copy', jsonParser, function (req, res) {
    const id = req.query.id;
    visualDao.copy(id).then(data => {
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
    visualDao.save(data).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/remove', jsonParser, function (req, res) {
    const ids = req.query.ids;
    visualDao.del(ids).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  
  // /**
  //  * 自己添加的 API
  //  */
  // app.get(url + '/preview-list', jsonParser, function (req, res) {
  //   const loginid = getLoginId(req);
  //   getUserInfo(loginid).then(axiosResp => {
  //     const userInfo = axiosResp.data;
  //     const permissionList = userInfo['permissionList'];
  //     let visualIdList = [];
  //     for (let i = 0; i < permissionList.length; i++) {
  //       const perm = permissionList[i];
  //       if (perm.startsWith("avue:vs:") && perm.length > 5) {
  //         visualIdList.push(perm.substring(5));
  //       }
  //     }
  //     visualIdList = Array.from(visualIdList)
  //     const query = {
  //       id: visualIdList
  //     }
  //     visualDao.list(query).then(data => {
  //       res.json(resbody.getSuccessResult(data));
  //     }).catch(error => {
  //       res.json(resbody.getFailResult(error));
  //     });
  //   })
  // })

  // app.post(url + '/assign-permission/user', jsonParser, function (req, res) {
  //   const loginid = getLoginId(req);
  //   const visualId = "avue:vs:" + req.body.visualId
  //   const users = req.body.users;
  //   const query = {
  //     loginid: loginid,
  //     id: visualId
  //   }
  //   visualDao.list(query).then(resultSet => {
  //     if (resultSet.length <= 0) {
  //       res.json(resbody.getFailResult('不允许分配该视图 ID 的权限'))
  //     } else {
  //       // 为每个用户添加权限
  //       for (let i = 0; i < users.length; i++) {
  //         addVisualForUser(users[i], visualId)
  //       }
  //     }
  //   })
  // })

}
