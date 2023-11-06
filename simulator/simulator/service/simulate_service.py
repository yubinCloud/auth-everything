from playwright.async_api import Page
from typing import List
from fastapi import Depends
import base64
import asyncio
from loguru import logger

from config import settings
from .path_service import PathService
from .storage_service import StorageService


# 登录页 - 用户名输入框
LOGIN_USERNAME_SELECTOR = '#username'
# 登录页 - 密码输入框
LOGIN_PWD_SELECTOR = '#password'
# 登录页 - 登录确认按钮
LOGIN_CONFIRM_BUTTON_SELECTOR = '#loginBtn'
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
        模拟登陆，将 credit 加到 local-storage 中
        :param page: _description_
        :param credits: _description_
        """
        await page.evaluate(r'localStorage.clear()')
        await page.goto(self.path_service.get_login_path())
    
    async def simulate_login(self, page: Page):
        await page.goto(self.path_service.get_simulate_login_path())
        await page.wait_for_load_state('networkidle')
        await page.fill(LOGIN_USERNAME_SELECTOR, settings.front.auth.username)
        await page.fill(LOGIN_PWD_SELECTOR, settings.front.auth.password)
        await page.click(LOGIN_CONFIRM_BUTTON_SELECTOR)
        await asyncio.sleep(1)
        localstorage = await page.evaluate('() => localStorage')
        logger.info(f'登陆之后 localstorage：{localstorage}')
    
    async def save_images(self, page: Page, visual_id: str, components: List[str], credits: str):
        """
        模拟进入各个图片页面，保存图片
        :param page: 浏览器的 page
        :param visual_id: _description_
        :param components: _description_
        """
        for component_id in components:
            url = self.path_service.get_component_path(visual_id, component_id)
            await page.goto(url)
            img_data = await self._parse_image(page, credits)
            if not img_data:
                continue
            img_bytes = base64.b64decode(img_data)
            self.storage_service.save_image(img_bytes, visual_id, component_id)
    
    async def _parse_image(self, page: Page, credits: str) -> str | None:
        await self._load_localstorage(page, credits)
        await page.wait_for_load_state('networkidle')
        start_button = await page.wait_for_selector('#start', state='visible')
        await start_button.click()
        button_element = await page.wait_for_selector('#download_img', state='visible')
        await asyncio.sleep(WAIT_IMG_TIME)
        await button_element.click()
        await page.wait_for_selector('#flag', state='visible')  # 等待函数执行结束
        img_data: str = await page.evaluate('() => window.imginfo')
        if not img_data.startswith('data:image/png;base64,'):
            return None
        return img_data[22:]

    async def _load_localstorage(self, page: Page, credits: str):
        kv = {
            'key': 'creditTech-insights',
            'value': credits
        }
        await page.evaluate(r'(data) => { localStorage.setItem(data.key, data.value); }', kv)