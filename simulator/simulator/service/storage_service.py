from fastapi import Depends
from minio import Minio
import io

from dependencies import use_minio
from config import settings


class StorageService:
    def __init__(
        self,
        minio_client: Minio = Depends(use_minio)
    ) -> None:
        self.minio_client = minio_client
    
    def save_image(self, img_bytes: bytes, visual_id: str, component_id: str) -> str:
        self.minio_client.put_object(
            settings.minio.bucket_name,
            f'{visual_id}/{component_id}.png',
            io.BytesIO(img_bytes),
            length=len(img_bytes),
            content_type='image/png'
        )
        