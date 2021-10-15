package com.truthbean.debbie.jdbc.domain;

/**
 * @author TruthBean
 * @since 0.5.1
 */
public class IdPage<T> extends Page<T> {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
