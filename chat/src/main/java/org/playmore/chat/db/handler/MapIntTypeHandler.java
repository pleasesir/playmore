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
 * @author Administrator
 */
@Slf4j
public class MapIntTypeHandler extends BaseTypeHandler<Map<Integer, Integer>> {

    public static String mapToString(Map<Integer, Integer> parameter) {
        JSONArray arrays = new JSONArray();
        if (parameter == null || parameter.isEmpty()) {
            return arrays.toJSONString();
        }

        for (Map.Entry<Integer, Integer> entry : parameter.entrySet()) {
            JSONArray array = new JSONArray();
            array.add(entry.getKey());
            array.add(entry.getValue());
            arrays.add(array);
        }

        return arrays.toJSONString();
    }

    public static Map<Integer, Integer> getMap(String columnValue) {
        if (columnValue == null) {
            return null;
        }

        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        if (columnValue.startsWith("[[")) {
            JSONArray arrays = JSONArray.parseArray(columnValue);
            for (int i = 0; i < arrays.size(); i++) {
                JSONArray array = arrays.getJSONArray(i);
                int key = array.getIntValue(0);
                int value = array.getIntValue(1);
                map.put(key, value);
            }
        } else if (columnValue.startsWith("[")) {
            JSONArray array = JSONArray.parseArray(columnValue);
            if (array.size() <= 0) {
                return map;
            }
            map.put(array.getInteger(0), array.getInteger(1));
        }

        return map;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<Integer, Integer> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, mapToString(parameter));
    }

    @Override
    public Map<Integer, Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {
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
    public Map<Integer, Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Map<Integer, Integer> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }

}
