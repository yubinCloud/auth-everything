from fastapi import APIRouter

from .simulate_controller import simulate_router


api_router = APIRouter()

api_router.include_router(simulate_router)
