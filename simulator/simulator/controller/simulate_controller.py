from fastapi import APIRouter, Depends, Body, BackgroundTasks
from playwright.async_api import Browser
from typing import List

from dependencies import use_browser
from schema.response import R
from schema.response.simulate import ImageURL
from schema.request.simulate import SaveVisualImgRequestBody
from service import SimulateService
from config import settings


simulate_router = APIRouter(
    prefix='/simulate', tags=['simulate']
)


async def save_imgs_func(
    visual_id: str,
    components: List[str],
    credits: str,
    browser: Browser,
    simulate_service: SimulateService,
):
    """
    保存图片的业务逻辑
    :param visual_id: _description_
    :param components: _description_
    :param credits: _description_
    :param browser: _description_
    :param path_service: _description_
    :param simulate_service: _description_
    """
    page = await browser.new_page()
    await simulate_service.save_images(page, visual_id, components, credits)
    await page.close()
    

@simulate_router.post(
    '/save-visual-imgs-async',
    response_model=R[str],
    summary='保存大屏中组件的图片（异步）'
)
async def save_visual_imgs_async(
    backgroud_tasks: BackgroundTasks,
    body: SaveVisualImgRequestBody = Body(...),
    browser: Browser = Depends(use_browser),
    simulate_service: SimulateService = Depends()
):
    backgroud_tasks.add_task(
        save_imgs_func,
        body.visual.visual_id,
        body.visual.components,
        body.credits,
        browser,
        simulate_service
    )
    return R.success('success')


@simulate_router.post(
    '/save-visual-imgs-sync',
    response_model=R[List[ImageURL]],
    summary='保存大屏中的组件（同步）'
)
async def save_visual_imgs_sync(
    body: SaveVisualImgRequestBody = Body(...),
    browser: Browser = Depends(use_browser),
    simulate_service: SimulateService = Depends()
):
    await save_imgs_func(
        body.visual.visual_id,
        body.visual.components,
        body.credits,
        browser,
        simulate_service
    )
    visual_id = body.visual.visual_id
    urls = [
        ImageURL(
            external=f'/s3/{settings.minio.bucket_name}/{visual_id}/{component_id}.png',
            internal=f'http://{settings.minio.url}/{settings.minio.bucket_name}/{visual_id}/{component_id}.png',
        )
        for component_id in body.visual.components
    ]
    return R.success(urls)

