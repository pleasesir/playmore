package org.playmore.chat.db.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
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
public class ListIntTypeHandler implements TypeHandler<List<Integer>> {

    private List<Integer> getIntegerList(String columnValue) {
        List<Integer> list = new ArrayList<>();
        if (columnValue == null || columnValue.isEmpty() || "".equals(StringUtils.trim(columnValue))) {
            return list;
        }
        JSONArray array = JSONArray.parseArray(columnValue);
        for (int i = 0; i < array.size(); i++) {
            int value = array.getIntValue(i);
            list.add(value);
        }
        return list;
    }

    private String listToString(List<Integer> parameter) {
        if (parameter == null || parameter.isEmpty()) {
            return new JSONArray().toJSONString();
        }
        return JSON.toJSONString(parameter);
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, List<Integer> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, listToString(parameter));
    }

    @Override
    public List<Integer> getResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        try {
            return getIntegerList(columnValue);
        } catch (Exception e) {
            String tableName = rs.getMetaData().getTableName(1);
            log.error("解析错误: columnName: {}, , columnValue: {}, 行数: {}, e: {}", tableName + "." + columnName, columnValue,
                    ", 行数: " + rs.getRow(), e);
            throw e;
        }
    }

    @Override
    public List<Integer> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public List<Integer> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }

}
