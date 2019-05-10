package com.truthbean.debbie.jdbc.service;

import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.entity.Surname;

import java.security.AccessControlException;
import java.util.List;
import java.util.Optional;

public interface SurnameService {

    /**
     *  test force commit and rollbackFor is not instanceOf this exception
     * @param surname params
     * @return boolean
     */
    @JdbcTransactional(rollbackFor = ArithmeticException.class, forceCommit = false)
    boolean save(Surname surname);

    Optional<Surname> selectById(Long id);

    @JdbcTransactional(readonly = true)
    List<Surname> selectAll();

    @JdbcTransactional(readonly = true)
    void doNothing();
}
