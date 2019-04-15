package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.annotation.SqlRepository;
import com.truthbean.debbie.jdbc.entity.Surname;

import java.sql.Connection;

@SqlRepository
public class SurnameRepository extends DmlRepositoryHandler<Surname, Long> {

    public SurnameRepository(Connection connection) {
        super(connection);
    }
}
