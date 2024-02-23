import { ApiProperty } from "@nestjs/swagger";

export class RenderDataxJsonConfRequest {
    @ApiProperty()
    readerID: string;

    
    @ApiProperty()
    readerConf: object;

    @ApiProperty()
    writerID: string;
    
    @ApiProperty()
    writerConf: object;
    
    @ApiProperty({description: "字段映射关系"})
    fieldMappings: FieldMapping[];
}


class FieldMapping {
    @ApiProperty({description: "source field"})
    s: string

    @ApiProperty({description: "target field"})
    t: string
}