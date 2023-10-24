from pydantic import BaseModel, Field
from typing import List


class VisualComponents(BaseModel):
    visual_id: str
    components: List[str]


class SaveVisualImgRequestBody(BaseModel):
    visual: VisualComponents = Field(..., description='大屏的相关信息')
    credits: dict = Field(..., description='需要放到 local-storage 里面的数据')
