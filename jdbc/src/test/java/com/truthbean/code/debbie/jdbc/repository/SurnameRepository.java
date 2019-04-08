package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.jdbc.annotation.SqlRepository;
import com.truthbean.code.debbie.jdbc.datasource.DataSourceFactory;

import java.sql.Connection;

@SqlRepository
public class SurnameRepository extends DmlRepositoryHandler<Surname, Long> {
    public SurnameRepository(DataSourceFactory dataSourceFactory) {
        super(dataSourceFactory);
    }

    public SurnameRepository(Connection connection) {
        super(connection);
    }
}
