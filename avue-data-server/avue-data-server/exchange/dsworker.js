import axios from "axios";
import { conf } from "./config.js";

let BASE_URL;

if (process.env.NODE_ENV === 'production') {
    BASE_URL = `http://${conf.SIDECAR_HOST}:${conf.SIDECAR_PORT}/ds-worker/ds-worker`
} else {
    BASE_URL = 'http://127.0.0.1:9300/ds-worker';
}

const dsWorkerClient = axios.create({
    baseURL: BASE_URL
})


export function execSelectSQL(dsConf, sql) {
    return dsWorkerClient.post('/exec/select', {
        "dataSourceConf": {
            "driverClass": dsConf.driverClass,
            "url": dsConf.url,
            "username": dsConf.username,
            "password": dsConf.password
        },
        "sql": sql
    })
}
