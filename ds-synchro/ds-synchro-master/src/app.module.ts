import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { ConfMakerModule } from './modules/confmaker/confmaker.module';
import { ConfigModule } from '@nestjs/config';

@Module({
  imports: [ConfMakerModule, ConfigModule.forRoot({isGlobal: true})],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
