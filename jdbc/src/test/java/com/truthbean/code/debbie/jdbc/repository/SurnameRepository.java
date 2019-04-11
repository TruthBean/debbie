package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.jdbc.annotation.SqlRepository;
import com.truthbean.code.debbie.jdbc.entity.Surname;

import java.sql.Connection;

@SqlRepository
public class SurnameRepository extends DmlRepositoryHandler<Surname, Long> {

    public SurnameRepository(Connection connection) {
        super(connection);
    }
}
