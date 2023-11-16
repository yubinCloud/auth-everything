import axios from "axios";
import { conf } from "./config.js";


let BASE_URL;

if (process.env.NODE_ENV === 'production') {
    BASE_URL = `http://${conf.SIDECAR_HOST}:${conf.SIDECAR_PORT}/sso-auth/auth`
} else {
    BASE_URL = 'http://127.0.0.1:9590/auth';
}

const authClient = axios.create({
    baseURL: BASE_URL
})

export function getUserInfo(username) {
    return authClient.get(`/internal/user/info/${username}`)
}

export function addVisualForUser(username, visualId) {
    body = {
        username: username,
        permissionList: [visualId]
    }
    return authClient.post('/admin/permission/add', body)
}

export function addVisualForRole(roleId, visualId) {
    body = {
        roleId: roleId,
        name: null,
        add: [visualId],
        del: []
    }
    return authClient.post('/admin/update', body)
}