package com.truthbean.debbie.jdbc.service;

import com.truthbean.debbie.jdbc.entity.Surname;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SurnameService extends EmptyService<Surname, Long> {

    /**
     *  test force commit and rollbackFor is not instanceOf this exception
     * @param surname params
     * @return boolean
     */
    boolean save(Surname surname);

    Optional<Surname> selectById(Long id);

    List<Surname> list();

    Optional<List<Surname>> getOptional();

    Map<String, List<Surname>> getMap();

    Optional<Map<String, List<Surname>>> emptyMap();

    Optional<List<Surname>> getByKey(String key);

    void doNothing();
}
