package org.playmore.chat.db.handler;

import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangpeng
 */
@Slf4j
public class MapStringTypeHandler extends BaseTypeHandler<Map<String, String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, String> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, mapToString(parameter));
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        try {
            return getMap(columnValue);
        } catch (Exception e) {
            String tableName = rs.getMetaData().getTableName(1);
            log.error("解析错误: columnName: {}, , columnValue: {}, 行数: {}, e: {}", tableName + "." + columnName, columnValue,
                    ", 行数: " + rs.getRow(), e);
            throw e;
        }
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Map<String, String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }

    private String mapToString(Map<String, String> parameter) {
        JSONArray arrays = new JSONArray();
        if (parameter == null || parameter.isEmpty()) {
            return "";
        }

        for (Map.Entry<String, String> entry : parameter.entrySet()) {
            JSONArray array = new JSONArray();
            array.add(entry.getKey());
            array.add(entry.getValue());
            arrays.add(array);
        }

        return arrays.toJSONString();
    }

    private Map<String, String> getMap(String columnValue) {
        Map<String, String> map = new HashMap<>();
        if (columnValue == null) {
            return map;
        }

        if (columnValue.startsWith("[[")) {
            JSONArray arrays = JSONArray.parseArray(columnValue);
            for (int i = 0; i < arrays.size(); i++) {
                JSONArray array = arrays.getJSONArray(i);
                String key = array.getString(0);
                String value = array.getString(1);
                map.put(key, value);
            }
        } else if (columnValue.startsWith("[")) {
            JSONArray array = JSONArray.parseArray(columnValue);
            if (array.isEmpty()) {
                return map;
            }
            map.put(array.getString(0), array.getString(1));
        }

        return map;
    }

}
