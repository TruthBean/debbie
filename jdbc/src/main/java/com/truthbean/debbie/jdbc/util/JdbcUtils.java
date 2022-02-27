/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.util;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

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
                LOGGER.debug("resultSet close ..");
                resultSet.close();
            }
        } catch (SQLException e) {
            LOGGER.error("ResultSet关闭失败!", e);
        } finally {
            try {
                if (preparedStatement != null) {
                    LOGGER.debug("preparedStatement close ..");
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                LOGGER.error("PreparedStatement关闭失败!", e);
            }
        }
    }

    public static void release(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("数据库连接关闭失败!", e);
            }
        }
    }
}
