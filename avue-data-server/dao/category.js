import { hTol, toColumn } from '../util/utils.js'
import { list, insert, del, update } from '../db/pool.js'
import categoryMode from '../modules/category.js'
let table = 'blade_visual_category'
export default {
  list: (data) => list({ table, data, parent: true, hump: true }),
  detail: (id, loginid) => list({ table, data: { id, loginid }, parent: true, hump: true }),
  update: (data) => update({
    table: table,
    data: {
      id: data.id,
      loginid: data.loginid
    },
    params: hTol(categoryMode.column, data)
  }),
  save: (data) => insert({
    table,
    column: toColumn(categoryMode.id, categoryMode.column),
    data: hTol(categoryMode.column, data)
  }),
  del: (id, loginid) => del({
    table: table,
    data: {
      id,
      loginid
    }
  }),
}

