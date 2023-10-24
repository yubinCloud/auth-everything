import { hTol, toColumn } from '../util/utils.js'
import { list, insert, del, update } from '../db/pool.js'
import mapMode from '../modules/map.js'
let table = 'blade_visual_map'
export default {
  list: (data) => list({ table, data, column: 'id,name' }),
  detail: (id) => list({ table, data: { id }, parent: true, hump: true }),
  update: (data) => update({
    table: table,
    data: {
      id: data.id,
    },
    params: hTol(mapMode.column, data)
  }),
  save: (data) => insert({
    table,
    column: toColumn(mapMode.id, mapMode.column),
    data: hTol(mapMode.column, data)
  }),
  del: (id) => del({
    table: table,
    data: {
      id
    }
  }),
}

