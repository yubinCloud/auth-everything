from config import settings


class PathService:
    
    def __init__(self) -> None:
        self.base_url = f'{settings.front.protocol}://{settings.front.host}:{settings.front.port}'
        self.img_module_base_url = f'{settings.front.img_module.protocol}://{settings.front.img_module.host}:{settings.front.img_module.port}'
        
    def get_login_path(self):
        """
        获取登录页面的 URL
        :return: _description_
        """
        return self.base_url + settings.front.path.login
    
    def get_component_path(self, visual_id: str, component_id: str):
        """
        根据大屏 ID 和组件 ID 来获得用来访问这个组件页面的 URL
        :param visual_id: _description_
        :param component_id: _description_
        """
        return f'{self.img_module_base_url}/module/{visual_id}?indexId={component_id}'

    def get_storage_dir(self, visual_id: str):
        """
        获取存储这个大屏的
        :param visual_id: _description_
        """
        