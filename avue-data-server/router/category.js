
import resbody from '../util/resbody.js';
import categoryDao from '../dao/category.js';
import bodyParser from 'body-parser';
import { getLoginId } from '../util/utils.js';
var jsonParser = bodyParser.json({ limit: '1000mb' });
let url = '/category'
export default (app) => {
  app.get(url + '/list', jsonParser, function (req, res) {
    const query = req.query;
    query.loginid = getLoginId(req);
    categoryDao.list(query).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });;
  })
  app.get(url + '/detail', jsonParser, function (req, res) {
    const id = req.query.id;
    const loginid = getLoginId(req);
    categoryDao.detail(id, loginid).then(data => {
      res.json(resbody.getSuccessResult(data[0]));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/remove', jsonParser, function (req, res) {
    const id = req.query.ids;
    const loginid = getLoginId(req);
    categoryDao.del(id, loginid).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/save', jsonParser, function (req, res) {
    const data = req.body;
    data.loginid = getLoginId(req);
    categoryDao.save(data).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/update', jsonParser, function (req, res) {
    const data = req.body;
    data.loginid = getLoginId(req);
    categoryDao.update(data).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
}
