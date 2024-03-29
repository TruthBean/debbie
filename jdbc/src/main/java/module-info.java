/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
module com.truthbean.debbie.jdbc {
    exports com.truthbean.debbie.jdbc.annotation;
    exports com.truthbean.debbie.jdbc.datasource;
    exports com.truthbean.debbie.jdbc.datasource.pool to com.truthbean.core, com.truthbean.debbie.core;
    exports com.truthbean.debbie.jdbc.domain;
    exports com.truthbean.debbie.jdbc.repository;
    exports com.truthbean.debbie.jdbc.transaction;
    exports com.truthbean.debbie.jdbc.column;
    exports com.truthbean.debbie.jdbc.datasource.multi;
    exports com.truthbean.debbie.jdbc.entity;

    exports com.truthbean.debbie.jdbc.mock;

    requires transitive com.truthbean.debbie.core;
    requires transitive java.sql;
    requires transitive java.sql.rowset;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.jdbc.JdbcModuleStarter;

    provides com.truthbean.debbie.reflection.ExecutableArgumentResolver
            with com.truthbean.debbie.jdbc.domain.PageableHandlerMethodArgumentResolver,
                    com.truthbean.debbie.jdbc.domain.SortHandlerMethodArgumentResolver;
}