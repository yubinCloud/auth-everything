import { hTol, toColumn } from '../util/utils.js'
import { list, insert, del, update } from '../db/pool.js'
import dbMode from '../modules/db.js'
let table = 'blade_visual_db'
export default {
  list: (data) => list({ table, data, hump: true }),
  orderList: (data, order) => list({ table, data, hump: true }, order),
  detail: (id) => list({ table, data: { id }, parent: true, hump: true }),
  update: (data) => update({
    table: table,
    data: {
      id: data.id,
    },
    params: hTol(dbMode.column, data)
  }),
  save: (data) => insert({
    table,
    column: toColumn(dbMode.id, dbMode.column),
    data: hTol(dbMode.column, data)
  }),
  del: (id) => del({
    table: table,
    data: {
      id,
    }
  }),
}

