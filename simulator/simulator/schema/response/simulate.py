from pydantic import BaseModel, Field


class ImageURL(BaseModel):
    external: str = Field(..., description='外部可访问的 URL')
    internal: str = Field(..., description='内部可访问的 URL')
