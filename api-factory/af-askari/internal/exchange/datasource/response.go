package datasource

type R[DATA interface{}] struct {
	Code    int
	msg     string
	data    DATA
	success bool
}

type DatasourceDetail struct {
	Id          string
	Name        string
	DriverClass string
	Url         string
	Username    string
	Password    string
	Remark      string
	Status      int
	IsDeleted   int
}

type QueryDetailResponse R[DatasourceDetail]
