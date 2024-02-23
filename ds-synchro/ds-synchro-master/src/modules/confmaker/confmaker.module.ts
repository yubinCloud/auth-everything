import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { ConfMakerController } from './confmaker.controller';
import { ConfMakerService } from './confmaker.service';
import { AvueHelperExchange } from 'src/shared/exchange/avuehelper.exchange';

@Module({
    imports: [HttpModule],
    controllers: [ConfMakerController],
    providers: [ConfMakerService, AvueHelperExchange],
})
export class ConfMakerModule {}