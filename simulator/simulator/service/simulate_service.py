from playwright.async_api import Page
from typing import List
from fastapi import Depends
import base64
import asyncio

from config import settings
from .path_service import PathService
from .storage_service import StorageService


# 登录页 - 用户名输入框
LOGIN_USERNAME_SELECTOR = '#app > div > div.login-right > div > div > div > div:nth-child(2) > input'
# 登录页 - 密码输入框
LOGIN_PWD_SELECTOR = '#app > div > div.login-right > div > div > div > div:nth-child(3) > input[type=password]'
# 登录页 - 登录确认按钮
LOGIN_CONFIRM_BUTTON_SELECTOR = '#app > div > div.login-right > div > div > div > div.login-form__button > button'
# 等待图片元素加载时间（单位：秒）
WAIT_IMG_TIME = 1.5

class SimulateService:
    def __init__(
        self,
        path_service: PathService = Depends(),
        storage_service: StorageService = Depends(),
    ) -> None:
        self.path_service = path_service
        self.storage_service = storage_service
    
    async def login(self, page: Page):
        """
        模拟登陆

        :param page: _description_
        """
        await page.fill(LOGIN_USERNAME_SELECTOR, settings.front.auth.username)
        await page.fill(LOGIN_PWD_SELECTOR, settings.front.auth.password)
        await page.click(LOGIN_CONFIRM_BUTTON_SELECTOR)
    
    async def simualte_login(self, page: Page, credits: dict):
        """
        模拟登陆，将 credit 加到 local-storage 中
        :param page: _description_
        :param credits: _description_
        """
        kv = {
            'key': 'creditTech-insights',
            'value': credits
        }
        await page.evaluate(r'(data) => { localStorage.setItem(data.key, data.value); }', kv)
        
    
    async def save_images(self, page: Page, visual_id: str, components: List[str]):
        """
        模拟进入各个图片页面，保存图片
        :param page: 浏览器的 page
        :param visual_id: _description_
        :param components: _description_
        """
        for component_id in components:
            url = self.path_service.get_component_path(visual_id, component_id)
            await page.goto(url)
            img_data = await self._parse_image(page)
            if not img_data:
                continue
            img_bytes = base64.b64decode(img_data)
            self.storage_service.save_image(img_bytes, visual_id, component_id)
    
    async def _parse_image(self, page: Page) -> str | None:
        button_element = await page.wait_for_selector('#download_img', state='visible')
        await asyncio.sleep(WAIT_IMG_TIME)
        await button_element.click()
        await page.wait_for_selector('#flag', state='visible')  # 等待函数执行结束
        img_data: str = await page.evaluate('() => window.imginfo')
        if not img_data.startswith('data:image/png;base64,'):
            return None
        return img_data[22:]