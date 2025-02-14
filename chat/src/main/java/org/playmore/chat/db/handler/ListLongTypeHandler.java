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
 * Created with IntelliJ IDEA.
 *
 * @Author: zhangpeng
 * @Date: 2023/12/06/11:35
 * @Description:
 */
@Slf4j
public class ListLongTypeHandler implements TypeHandler<List<Long>> {

    private List<Long> getLongList(String columnValue) {
        if (columnValue == null || columnValue.isEmpty()) {
            return null;
        }

        List<Long> list = new ArrayList<>();
        JSONArray array = JSONArray.parseArray(columnValue);
        for (int i = 0; i < array.size(); i++) {
            long value = Long.parseLong(array.getString(i));
            list.add(value);
        }
        return list;
    }

    private String listToString(List<Long> parameter) {
        if (parameter == null || parameter.isEmpty()) {
            return new JSONArray().toString();
        }

        return JSONArray.toJSONString(parameter);
    }

    @Override
    public void setParameter(PreparedStatement arg0, int arg1, List<Long> arg2, JdbcType arg3) throws SQLException {
        arg0.setString(arg1, listToString(arg2));
    }

    @Override
    public List<Long> getResult(ResultSet rs, String columnName) throws SQLException {
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
    public List<Long> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public List<Long> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }

}
