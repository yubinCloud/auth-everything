from fastapi import Request
from playwright.async_api import async_playwright, Browser
from minio import Minio


async def use_browser() -> Browser:
    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)
        yield browser
        await browser.close()


def use_minio(
    request: Request
) -> Minio:
    return request.app.state.minio_client
