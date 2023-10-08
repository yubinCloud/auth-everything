package datasource

import (
	"context"
	"errors"
	"github.com/gogf/gf/v2/frame/g"
)

type DatasourceExchange struct{}

func (exchange *DatasourceExchange) queryDetail(ctx context.Context, dsId string) (dsDetail *DatasourceDetail, err error) {
	resp := QueryDetailResponse{}
	err = g.Client().GetVar(ctx, "https://af-askari-sidecar/avue-data-server/db/").Scan(&resp)
	if err != nil {
		return nil, err
	}
	if !resp.success {
		return nil, errors.New("avue 接口出错")
	}
	respData := resp.data
	dsDetail = &DatasourceDetail{
		Id:          respData.Id,
		Name:        respData.Name,
		DriverClass: respData.DriverClass,
		Url:         respData.Url,
	}
}
