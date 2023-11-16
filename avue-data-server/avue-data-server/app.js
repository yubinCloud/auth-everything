import express from 'express';
import visualRoute from './router/visual.js';
import categoryRoute from './router/category.js'
import mapRoute from './router/map.js'
import componentRoute from './router/component.js'
import dbRoute from './router/db.js'
import recordRoute from './router/record.js'
import bodyParser from 'body-parser';
import morgan from 'morgan';
import resbody from './util/resbody.js';
const app = express();

app.use(morgan('short'));
app.use(bodyParser.urlencoded({ limit: '1000mb', extended: true }));
app.use(function(err, req, res, next) {
    res.json(resbody.getFailResult('', '图片不存在'));
});
//跨越解决
app.all('*', function (req, res, next) {
    res.header('Access-Control-Allow-Credentials', true)
    res.header('Access-Control-Allow-Origin', '*');
    res.header("Access-Control-Allow-Headers", "*");
    res.header('Access-Control-Allow-Methods', 'PUT,POST,GET,DELETE,OPTIONS');
    if (req.method.toLowerCase() == 'options') {
        res.send(200);
    } else {
        next();
    }
});
visualRoute(app)
categoryRoute(app);
mapRoute(app)
dbRoute(app)
recordRoute(app)
componentRoute(app)

app.get('/health', (req, res) => {
    res.json({status: "UP"})
})

app.get('/other', (req, res) => {
    res.json({status: "other"})
})


app.listen(10002, () => console.log('avue-data-server app listening on port 10002!'))

