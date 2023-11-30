from typing import Dict

from dynamic_route import DynamicRoute, RouteId

ROUTE_TABLE: Dict[RouteId, DynamicRoute] = {}


def put_route(route_id: RouteId, route: DynamicRoute):
    ROUTE_TABLE[route_id] = route


def delete_route(route_id: RouteId):
    if route_id in ROUTE_TABLE:
        del ROUTE_TABLE[route_id]


def get_route(route_id: RouteId) -> DynamicRoute:
    return ROUTE_TABLE.get(route_id)
