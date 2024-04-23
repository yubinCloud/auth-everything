from fastapi import APIRouter
from controller.v1.sql_controller import sql_router
from controller.v1.api_controller import api_router as api_get_data

api_router = APIRouter()

api_router.include_router(sql_router)
api_router.include_router(api_get_data)
