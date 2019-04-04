package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.jdbc.annotation.SqlColumn;
import com.truthbean.code.debbie.jdbc.annotation.SqlEntity;
import com.truthbean.code.debbie.jdbc.column.PrimaryKeyType;

import java.net.URL;
import java.sql.Timestamp;

@SqlEntity
public class Qu {

    @SqlColumn(id = true, comment = "主键", primaryKey = PrimaryKeyType.AUTO_INCREMENT)
    private Long id;

    @SqlColumn(nullable = false)
    private String origin;

    private Timestamp begin;

    @SqlColumn(nullable = true)
    private URL website;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Timestamp getBegin() {
        return begin;
    }

    public void setBegin(Timestamp begin) {
        this.begin = begin;
    }

    public URL getWebsite() {
        return website;
    }

    public void setWebsite(URL website) {
        this.website = website;
    }
}
