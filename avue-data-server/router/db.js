import ObjectsToCsv from 'objects-to-csv';
import { v4 as uuidv4 } from 'uuid';
import bodyParser from 'body-parser';
import axios from "axios";

import resbody from '../util/resbody.js';
import dbDao from '../dao/db.js';
import crypto from '../util/crypto.js';
import { content } from '../db/pool.js';
import { getSqlConfig, getLoginId, getDsConf } from '../util/utils.js';
import { execSelectSQL } from '../exchange/dsworker.js';
var jsonParser = bodyParser.json({ limit: '1000mb' });
let url = '/db'

export default (app) => {
  app.get(url + '/list', jsonParser, function (req, res) {
    const query = req.query;
    query.loginid = getLoginId(req);
    dbDao.list(query).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });;
  })
  app.get(url + '/detail', jsonParser, function (req, res) {
    const id = req.query.id;
    const loginid = getLoginId(req);
    dbDao.detail(id, loginid).then(data => {
      res.json(resbody.getSuccessResult(data[0]));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/remove', jsonParser, function (req, res) {
    const id = req.query.ids;
    const loginid = getLoginId(req);
    dbDao.del(id, loginid).then(data => {
      res.json(resbody.getSuccessResult(data));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/db-test', function (req, res) {
    const body = JSON.parse(crypto.decrypt(req.headers.data));
    let config = getSqlConfig(body);
    // ds-worker
    content(config).then(data => {
      res.json(resbody.getSuccessResult({}));
    }).catch(error => {
      res.json(resbody.getFailResult(error));
    });
  })
  app.post(url + '/dynamic-query', function (req, res) {
    const body = JSON.parse(crypto.decrypt(req.headers.data));
    const id = body.id
    const loginid = getLoginId(req)
    const sql = body.sql;
    dbDao.detail(id, loginid).then(data => {
      let dsConf = getDsConf(data[0]);
      execSelectSQL(dsConf, sql).then(dsresp => {
        res.json(resbody.getSuccessResult(dsresp.data.data));
      }).catch(error => {
        res.json(resbody.getFailResult(error));
      })
      // let config = getSqlConfig(data[0]);
      // content(config, sql).then(data => {
      //   res.json(resbody.getSuccessResult(data));
      // }).catch(error => {
      //   res.json(resbody.getFailResult(error));
      // });
    })
  })
  app.post(url + '/submit', jsonParser, function (req, res) {
    const data = req.body;
    data.loginid = getLoginId(req);
    if (data.id) {
      dbDao.update(data).then(data => {
        res.json(resbody.getSuccessResult(data));
      }).catch(error => {
        res.json(resbody.getFailResult(error));
      });
    } else {
      dbDao.save(data).then(data => {
        res.json(resbody.getSuccessResult(data));
      }).catch(error => {
        res.json(resbody.getFailResult(error));
      });
    }
  })

  // table-export
  app.post(url + '/sql-export', function (req, res) {
    const body = JSON.parse(crypto.decrypt(req.headers.data));
    const id = body.id;
    const loginid = getLoginId(req);
    const sql = body.sql;
    dbDao.detail(id, loginid).then(data => {
      let dsConf = getDsConf(data[0]);
      execSelectSQL(dsConf, sql).then(dsresp => {
        let data = dsresp.data.data;
        console.log(data)
        // 将 data 写入 csv 中
        const fname = Date.now().toString() + '.csv';
        new ObjectsToCsv(data).toDisk('./exported-files/' + fname).then(
          () => res.json(resbody.getSuccessResult({'url': '/avue-download/' + fname}))
        ).catch(error => {
          res.json(resbody.getFailResult(error));
        })
      }).catch(error => {
        res.json(resbody.getFailResult(error));
      });
    })
  }) 
}
