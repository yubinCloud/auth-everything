
import resbody from '../util/resbody.js';
import recordDao from '../dao/record.js';
import bodyParser from 'body-parser';
var jsonParser = bodyParser.json({ limit: '1000mb' });
let url = '/record'
export default (app) => {
  app.get(url + '/list', jsonParser, function (req, res) {
    const query = req.query;
    recordDao.list(query).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.get(url + '/detail', jsonParser, function (req, res) {
    const id = req.query.id;
    recordDao.detail(id).then(data => {
      res.json(resbody.getSuccessResult(data[0]));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })

  app.get(url + '/data', jsonParser, function (req, res) {
    const id = req.query.id;
    recordDao.detail(id).then(data => {
      res.json(JSON.parse(data[0].data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/remove', jsonParser, function (req, res) {
    const id = req.query.ids;
    recordDao.del(id).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/save', jsonParser, function (req, res) {
    const data = req.body;
    recordDao.save(data).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })

  app.post(url + '/update', jsonParser, function (req, res) {
    const data = req.body;
    recordDao.update(data).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
}
