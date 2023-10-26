import { hTol, toColumn } from '../util/utils.js'
import { list, insert, del, update } from '../db/pool.js'
import categoryMode from '../modules/category.js'
let table = 'blade_visual_category'
export default {
  list: (data) => list({ table, data, parent: true, hump: true }),
  detail: (id) => list({ table, data: { id }, parent: true, hump: true }),
  update: (data) => update({
    table: table,
    data: {
      id: data.id
    },
    params: hTol(categoryMode.column, data)
  }),
  save: (data) => insert({
    table,
    column: toColumn(categoryMode.id, categoryMode.column),
    data: hTol(categoryMode.column, data)
  }),
  del: (id) => del({
    table: table,
    data: {
      id
    }
  }),
}

