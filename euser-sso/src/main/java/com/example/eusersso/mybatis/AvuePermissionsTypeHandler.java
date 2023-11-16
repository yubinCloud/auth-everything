package com.example.eusersso.mybatis;

import cn.hutool.json.JSONUtil;
import com.example.eusersso.entity.AvuePermission;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@MappedJdbcTypes({JdbcType.OTHER})
@MappedTypes(value = {List.class})
public class AvuePermissionsTypeHandler extends BaseTypeHandler<List<AvuePermission>>  {


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<AvuePermission> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSONUtil.toJsonStr(parameter));
    }

    @Override
    public List<AvuePermission> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return convertValue(value);
    }

    @Override
    public List<AvuePermission> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return convertValue(value);
    }

    @Override
    public List<AvuePermission> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return convertValue(value);
    }

    private List<AvuePermission> convertValue(String columnValue) {
        if (Objects.isNull(columnValue)) {
            return Collections.emptyList();
        }
        return JSONUtil.parseArray(columnValue).toList(AvuePermission.class);
    }
}
