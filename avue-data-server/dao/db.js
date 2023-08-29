import { hTol, toColumn } from '../util/utils.js'
import { list, insert, del, update } from '../db/pool.js'
import dbMode from '../modules/db.js'
let table = 'blade_visual_db'
export default {
  list: (data) => list({ table, data, hump: true }),
  detail: (id, loginid) => list({ table, data: { id, loginid }, parent: true, hump: true }),
  update: (data) => update({
    table: table,
    data: {
      id: data.id,
      loginid: data.loginid
    },
    params: hTol(dbMode.column, data)
  }),
  save: (data) => insert({
    table,
    column: toColumn(dbMode.id, dbMode.column),
    data: hTol(dbMode.column, data)
  }),
  del: (id, loginid) => del({
    table: table,
    data: {
      id,
      loginid
    }
  }),
}

