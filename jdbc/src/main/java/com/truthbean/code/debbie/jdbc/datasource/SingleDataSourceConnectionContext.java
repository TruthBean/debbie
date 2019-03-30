package com.truthbean.code.debbie.jdbc.datasource;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-14 11:50
 */
public final class SingleDataSourceConnectionContext implements Closeable {

    private static final DataSourceProperties PROPERTIES = new DataSourceProperties();

    private static final SingleDataSourceConnectionContext INSTANCE = new SingleDataSourceConnectionContext();

    public static SingleDataSourceConnectionContext getInstance() {
        return INSTANCE;
    }

    private SingleDataSourceConnectionContext() {
        try {
            Class.forName(PROPERTIES.getDriverName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 实例化一个线程
     */
    private final ThreadLocal<Connection> conThread = new ThreadLocal<>();

    /**
     * 将数据连接绑定线程，有利于并发操作
     * @param connection jdbc connection
     */
    public void bind(Connection connection) {
        conThread.set(connection);
    }

    public void initConnectionAndBind() {
        try {
            Connection connection = DriverManager.getConnection(PROPERTIES.getUrl(), PROPERTIES.getUser(),
                    PROPERTIES.getPassword());
            conThread.set(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从当前运行线程中获取与其绑定的数据连接
     * @return connection
     */
    public Connection get() {
        Connection connection = conThread.get();
        if (connection == null) {
            initConnectionAndBind();
        }
        return conThread.get();
    }

    public void remove() {
        conThread.remove();
    }

    @Override
    public void close() {
        conThread.remove();
    }
}
