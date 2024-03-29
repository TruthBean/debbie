/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.service;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.check.jdbc.entity.Surname;

import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019-11-28 21:34.
 */
@BeanComponent
public class ApiService {

    private final SurnameService surnameService;

    public ApiService(@BeanInject SurnameService surnameService) {
        this.surnameService = surnameService;
    }

    public List<Surname> selectAll() {
        return this.surnameService.list();
    }
}
