package org.playmore.chat.db.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-07-13 17:49
 */
@Slf4j
public class LongDateTypeHandler implements TypeHandler<Long> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType) throws SQLException {
        ps.setTimestamp(i, new Timestamp(parameter));
    }

    @Override
    public Long getResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp date = null;
        try {
            date = rs.getTimestamp(columnName);
            return date != null ? date.getTime() : 0L;
        } catch (SQLException e) {
            String tableName = rs.getMetaData().getTableName(1);
            log.error("解析错误: columnName: {}, , columnValue: {}, 行数: {}, e: {}", tableName + "." + columnName, date,
                    ", 行数: " + rs.getRow(), e);
            throw e;
        }
    }

    @Override
    public Long getResult(ResultSet rs, int columnIndex) throws SQLException {
        Timestamp date = rs.getTimestamp(columnIndex);
        return date != null ? date.getTime() : 0L;
    }

    @Override
    public Long getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Timestamp date = cs.getTimestamp(columnIndex);
        return date != null ? date.getTime() : 0L;
    }

}
