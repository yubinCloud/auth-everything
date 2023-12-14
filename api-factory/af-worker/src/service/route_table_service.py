from typing import Dict, Set

from dynamic_route import DynamicRoute

ROUTE_TABLE: Dict[str, DynamicRoute] = {}


def put_route(path: str, route: DynamicRoute):
    ROUTE_TABLE[path] = route


def delete_route(path: str):
    if path in ROUTE_TABLE:
        del ROUTE_TABLE[path]


def get_route(path: str) -> DynamicRoute:
    return ROUTE_TABLE.get(path)


def route_path_list() -> Set[str]:
    return set(ROUTE_TABLE.keys())
