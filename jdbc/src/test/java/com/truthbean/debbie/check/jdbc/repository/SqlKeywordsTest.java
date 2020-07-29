package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.debbie.jdbc.repository.SqlKeywords;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlKeywordsTest {

    @Test
    void values() {
        SqlKeywords[] values = SqlKeywords.values();
        for (SqlKeywords value : values) {
            System.out.println(value.value());
        }
        System.out.println(SqlKeywords.of("testtestsatedast"));
    }
}