from typing import TypedDict, List

class SimpleRouteInfo(TypedDict):
    rid: str
    name: str
    path: str
    methods: List[str]


class RouteInfo(SimpleRouteInfo):
    code: str