import typing
from starlette.routing import Route

class AfRoute(Route):
    def __init__(
        self,
        path: str,
        endpoint: typing.Callable,
        route_id: str,
        *,
        methods: typing.Optional[typing.List[str]] = None,
        name: typing.Optional[str] = None,
        include_in_schema: bool = True,
    ) -> None:
        self.rid = route_id  # route 的唯一标识
        super().__init__(path, endpoint, methods=methods, name=name, include_in_schema=include_in_schema)

