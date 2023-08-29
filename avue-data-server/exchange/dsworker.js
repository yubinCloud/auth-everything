import axios from "axios";

const SIDECAR_HOST = process.env.SIDECAR_HOST ? process.env.SIDECAR_HOST : '127.0.0.1';
const SIDECAR_PORT = process.env.SIDECAR_PORT ? process.env.SIDECAR_PORT : '10012';

const dsWorkerClient = axios.create({
    baseURL: `http://${SIDECAR_HOST}:${SIDECAR_PORT}/ds-worker/ds-worker`
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
