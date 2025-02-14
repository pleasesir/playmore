package org.playmore.chat.db.handler;

import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
@Slf4j
public class ListStringTypeHandler implements TypeHandler<List<String>> {

    private List<String> getLongList(String columnValue) {
        List<String> list = new ArrayList<String>();
        if (columnValue == null || columnValue.isEmpty()) {
            return list;
        }

        JSONArray array = JSONArray.parseArray(columnValue);
        for (int i = 0; i < array.size(); i++) {
            String value = array.getString(i);
            list.add(value);
        }
        return list;
    }

    private String listToString(List<String> parameter) {
        JSONArray arrays = null;
        if (parameter == null || parameter.isEmpty()) {
            arrays = new JSONArray();
            return arrays.toString();
        }

        return JSONArray.toJSONString(parameter);
    }

    @Override
    public void setParameter(PreparedStatement arg0, int arg1, List<String> arg2, JdbcType arg3) throws SQLException {
        arg0.setString(arg1, listToString(arg2));
    }

    @Override
    public List<String> getResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        try {
            return getLongList(columnValue);
        } catch (Exception e) {
            String tableName = rs.getMetaData().getTableName(1);
            log.error("解析错误: columnName: {}, , columnValue: {}, 行数: {}, e: {}", tableName + "." + columnName, columnValue,
                    ", 行数: " + rs.getRow(), e);
            throw e;
        }
    }

    @Override
    public List<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }

}
