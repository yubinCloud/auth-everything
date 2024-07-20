package com.example.avuehelper.service.dscoordinator;

import com.example.avuehelper.entity.DataSourceConf;
import com.example.avuehelper.service.rpc.DsWorkerRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class DatasourceMangeService {

    private final DsWorkerRpcService dsWorkerService;

    public boolean checkConnection(DataSourceConf dsConf) {
        String checkSQL = "SELECT 1 FROM DUAL";
        try {
            dsWorkerService.execSelectSQL(dsConf, checkSQL);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

}
