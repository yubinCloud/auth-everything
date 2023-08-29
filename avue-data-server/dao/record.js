import { hTol, toColumn } from '../util/utils.js'
import { list, insert, del, update } from '../db/pool.js'
import recordMode from '../modules/record.js'
let table = 'blade_visual_record'
export default {
  list: (data) => list({ table, data, column: 'id,name,dataType' }),
  detail: (id) => list({ table, data: { id }, parent: true, hump: true }),
  update: (data) => update({
    table: table,
    data: {
      id: data.id
    },
    params: hTol(recordMode.column, data)
  }),
  save: (data) => insert({
    table,
    column: toColumn(recordMode.id, recordMode.column),
    data: hTol(recordMode.column, data)
  }),
  del: (id) => del({
    table: table,
    data: {
      id
    }
  }),
}

