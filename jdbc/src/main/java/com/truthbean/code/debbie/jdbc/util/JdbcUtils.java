package com.truthbean.code.debbie.jdbc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-14 11:57
 */
public class JdbcUtils {
    private JdbcUtils() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtils.class);

    /**
     * 关闭dao内的方法的ResultSet, PreparedStatement 注意关闭的顺序
     * @param resultSet ResultSet
     * @param preparedStatement PreparedStatement
     */
    public static void close(ResultSet resultSet, PreparedStatement preparedStatement) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            resultSet = null;
            LOGGER.error("ResultSet关闭失败!", e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                preparedStatement = null;
                LOGGER.error("PreparedStatement关闭失败!", e);
            }
        }
    }

    public static void release(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                connection = null;
                LOGGER.error("数据库连接关闭失败!", e);
            }
        }
    }
}
