import { Injectable } from "@nestjs/common";
import { AvueHelperExchange, VisualDB } from '../../shared/exchange/avuehelper.exchange';


@Injectable()
export class ConfMakerService {
    constructor(private readonly avueHelperExchange: AvueHelperExchange) {}

    async queryDatabaseConf(id: BigInt): Promise<VisualDB> {
        const sourceDB = await this.avueHelperExchange.queryDatabaseConf(id);
        if (sourceDB.code != 0) {
            throw new Error(`Error response code with ${sourceDB.code} and message: ${sourceDB.msg}`)
        }
        return sourceDB.data;
    }
}