def to_route_path(znode_path: str):
    """
    将 znode-path 转为 route-path
    :param znode_path: _description_
    :return: _description_
    """
    if znode_path.startswith("/af/route/") and len(znode_path) < 10:
        return None
    return '/dynamic' + znode_path[10:].replace('.', '/')