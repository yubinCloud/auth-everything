import { Injectable } from "@nestjs/common";
import { ConfigService } from "@nestjs/config";
import { R } from "../dto";
import { HttpService } from "@nestjs/axios";


export interface VisualDB {
    id: number;
    name: string;
    driverClass: string;
    url: string;
    username: string;
    password: string;
}


@Injectable()
export class AvueHelperExchange {

    private sidecarURL: string;

    constructor(configService: ConfigService, private readonly httpService: HttpService) {
        this.sidecarURL = configService.get("SIDECAR_URL");
    }

    async queryDatabaseConf(id: BigInt): Promise<R<VisualDB>> {
        try {
            const path = `/avue-helper/avue-helper/db/query/${id}`;
            console.log(path);
            const resp = await this.httpService.axiosRef.get(this.sidecarURL + `/avue-helper/avue-helper/db/query/${id}`);
            return resp.data;
        } catch (error) {
            throw new Error(`Error fetching data: ${error}`)
        }
    }

}