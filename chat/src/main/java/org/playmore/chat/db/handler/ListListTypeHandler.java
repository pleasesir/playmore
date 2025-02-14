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
public class ListListTypeHandler implements TypeHandler<List<List<Integer>>> {
    private String listToString(List<List<Integer>> parameter) {
        JSONArray arrays = null;
        if (parameter == null || parameter.isEmpty()) {
            arrays = new JSONArray();
            return arrays.toString();
        }

        return JSONArray.toJSONString(parameter);
    }

    private List<List<Integer>> getListList(String columnValue, String columnName) {
        List<List<Integer>> listList = new ArrayList<List<Integer>>();
        if (columnValue == null || columnValue.isEmpty()) {
            return listList;
        }

        JSONArray arrays = JSONArray.parseArray(columnValue);
        for (int i = 0; i < arrays.size(); i++) {
            List<Integer> list = new ArrayList<Integer>();
            JSONArray array = arrays.getJSONArray(i);
            for (int j = 0; j < array.size(); j++) {
                list.add(array.getInteger(j));
            }

            listList.add(list);
        }

        return listList;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, List<List<Integer>> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, listToString(parameter));
    }

    @Override
    public List<List<Integer>> getResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        try {
            return getListList(columnValue, columnName);
        } catch (Exception e) {
            String tableName = rs.getMetaData().getTableName(1);
            log.error("解析错误: columnName: {}, , columnValue: {}, 行数: {}, e: {}", tableName + "." + columnName, columnValue,
                    ", 行数: " + rs.getRow(), e);
            throw e;
        }
    }

    @Override
    public List<List<Integer>> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public List<List<Integer>> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }
}
