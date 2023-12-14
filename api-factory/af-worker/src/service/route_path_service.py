def to_route_path(znode_path: str) -> str | None:
    """
    将 znode-path 转为 route-path
    :param znode_path: _description_
    :return: _description_
    """
    if not znode_path.startswith("/af/route/"):
        return None
    return '/dynamic' + znode_path[10:].replace('.', '/')


def to_znode_path(route_path: str) -> str | None:
    """
    将 route-path 转为 znode-path
    :param route_path: _description_
    """
    if not route_path.startswith('/dynamic'):
        return None
    return '/af/route/' + route_path[8].replace('/', '.')
