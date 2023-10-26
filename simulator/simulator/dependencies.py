from fastapi import Request
from playwright.async_api import async_playwright, BrowserContext
from minio import Minio

from config import settings


async def use_browser() -> BrowserContext:
    async with async_playwright() as p:
        browser = await p.webkit.launch(headless=settings.playwright.headless)
        yield browser
        await browser.close()


def use_minio(
    request: Request
) -> Minio:
    return request.app.state.minio_client