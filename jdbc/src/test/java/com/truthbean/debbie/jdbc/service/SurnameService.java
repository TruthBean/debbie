/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.service;


import com.truthbean.debbie.check.jdbc.entity.Surname;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;

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

    @JdbcTransactional
    List<Surname> list();

    Optional<List<Surname>> getOptional();

    Map<String, List<Surname>> getMap();

    Optional<Map<String, List<Surname>>> emptyMap();

    Optional<List<Surname>> getByKey(String key);

    void doNothing();
}
