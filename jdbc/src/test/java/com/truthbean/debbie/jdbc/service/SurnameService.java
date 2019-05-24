package com.truthbean.debbie.jdbc.service;

import com.truthbean.debbie.jdbc.entity.Surname;

import java.util.List;
import java.util.Optional;

public interface SurnameService {

    /**
     *  test force commit and rollbackFor is not instanceOf this exception
     * @param surname params
     * @return boolean
     */
    boolean save(Surname surname);

    Optional<Surname> selectById(Long id);

    List<Surname> selectAll();

    void doNothing();
}
