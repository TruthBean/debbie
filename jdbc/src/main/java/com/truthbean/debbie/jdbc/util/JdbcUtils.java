/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
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

import java.sql.*;

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
     * @param statement Statement
     */
    public static void close(ResultSet resultSet, Statement statement) {
        try {
            if (resultSet != null) {
                LOGGER.debug("resultSet close ..");
                resultSet.close();
            }
        } catch (SQLException e) {
            LOGGER.error("ResultSet close error!", e);
        } finally {
            try {
                if (statement != null) {
                    LOGGER.debug("Statement(" + statement + ") close ..");
                    statement.close();
                }
            } catch (SQLException e) {
                LOGGER.error("Statement(" + statement + ") close error!", e);
            }
        }
    }

    public static void release(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("Connection(" + connection + ") close error!", e);
            }
        }
    }
}
