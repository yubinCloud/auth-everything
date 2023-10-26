import axios from "axios";
import { conf } from "./config.js";

const authClient = axios.create({
    baseURL: `http://${conf.SIDECAR_HOST}:${conf.SIDECAR_PORT}/sso-auth/auth`
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