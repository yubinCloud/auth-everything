import axios from "axios";
import { conf } from "./config.js";


let BASE_URL;

if (process.env.NODE_ENV === 'production') {
    BASE_URL = `http://${conf.SIDECAR_HOST}:${conf.SIDECAR_PORT}/euser-sso/euser-sso`
} else {
    BASE_URL = 'http://127.0.0.1:9592/euser-sso';
}

const euserSSOClient = axios.create({
    baseURL: BASE_URL
})


export default {
    getPersonalAvuePermission: async function (euserUsername) {
        const resp = await euserSSOClient.get('/user/permission/avue', {
            headers: {
                'X-Euser': euserUsername
            }
        })
        if (resp.status != 200) {
            throw new Error("euser-sso 服务出错");
        }
        const respData = resp.data;
        const permissions = respData.data;
        return permissions;
    },
}