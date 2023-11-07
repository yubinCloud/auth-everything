package com.example.eusersso.mybatis;

import cn.hutool.json.JSONUtil;
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
public class IntegerListTypeHandler extends BaseTypeHandler<List<Integer>> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Integer> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSONUtil.toJsonStr(parameter));
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (Objects.isNull(value)) {
            return Collections.emptyList();
        }
        return JSONUtil.parseArray(value).toList(Integer.class);
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        if (Objects.isNull(value)) {
            return Collections.emptyList();
        }
        return JSONUtil.parseArray(value).toList(Integer.class);
    }

    @Override
    public List<Integer> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        if (Objects.isNull(value)) {
            return Collections.emptyList();
        }
        return JSONUtil.parseArray(value).toList(Integer.class);
    }
}
