package com.truthbean.debbie.hikari;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.SQLException;

@ExtendWith({DebbieApplicationExtension.class})
public class HikariDataSourcesTest {

    @Test
    public void testDataSource(@BeanInject("dataSourceFactory") DataSourceFactory factory) {
        System.out.println(factory);
        try {
            Connection connection = factory.getConnection();
            System.out.println(connection);
            Thread.sleep(1000);
            connection.close();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDataSourceConfiguration(@BeanInject DataSourceConfiguration configuration) {
        System.out.println(configuration);
    }
}
