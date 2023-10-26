import { hTol, toColumn } from '../util/utils.js'
import { list, insert, del, update } from '../db/pool.js'
import componentMode from '../modules/component.js'
let table = 'blade_visual_component'
export default {
  list: (data) => list({ table, data, column: 'id,name' }),
  detail: (id) => list({ table, data: { id }, parent: true, hump: true }),
  update: (data) => update({
    table: table,
    data: {
      id: data.id
    },
    params: hTol(componentMode.column, data)
  }),
  save: (data) => insert({
    table,
    column: toColumn(componentMode.id, componentMode.column),
    data: hTol(componentMode.column, data)
  }),
  del: (id) => del({
    table: table,
    data: {
      id
    }
  }),
}

