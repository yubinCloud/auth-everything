import axios from "axios";
import { conf } from "./config.js";

const dsWorkerClient = axios.create({
    baseURL: `http://${conf.SIDECAR_HOST}:${conf.SIDECAR_PORT}/ds-worker/ds-worker`
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
