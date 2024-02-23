import { Body, Controller, Get, Post } from '@nestjs/common';
import { RenderDataxJsonConfRequest } from './confmaker.request';
import { R, success } from 'src/shared/dto';
import { ConfMakerService } from './confmaker.service';


@Controller("conf-maker")
export class ConfMakerController {
    constructor(private readonly confMakerService: ConfMakerService) {}

    @Post("render")
    async renderDataxJsonConf(@Body() renderRequest: RenderDataxJsonConfRequest): Promise<R<object>> {
        const result = await this.confMakerService.queryDatabaseConf(1161483357481541634n);
        console.log(result)
        return success(null);
    }
}