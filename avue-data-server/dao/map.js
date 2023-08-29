import { hTol, toColumn } from '../util/utils.js'
import { list, insert, del, update } from '../db/pool.js'
import mapMode from '../modules/map.js'
let table = 'blade_visual_map'
export default {
  list: (data) => list({ table, data, column: 'id,name' }),
  detail: (id, loginid) => list({ table, data: { id, loginid }, parent: true, hump: true }),
  update: (data) => update({
    table: table,
    data: {
      id: data.id,
      loginid: data.loginid
    },
    params: hTol(mapMode.column, data)
  }),
  save: (data) => insert({
    table,
    column: toColumn(mapMode.id, mapMode.column),
    data: hTol(mapMode.column, data)
  }),
  del: (id, loginid) => del({
    table: table,
    data: {
      id,
      loginid
    }
  }),
}

