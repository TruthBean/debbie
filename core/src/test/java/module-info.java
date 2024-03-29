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
open module com.truthbean.debbie.core.test {
    exports com.truthbean.debbie.check.task;
    exports com.truthbean.debbie.check.event;
    exports com.truthbean.debbie.check.boot;
    exports com.truthbean.debbie.bean.custom;

    requires java.base;
    requires com.truthbean.debbie.test;
    requires com.truthbean.debbie.aio;
    requires com.truthbean.debbie.jdbc;
    requires jakarta.inject;
    requires com.fasterxml.jackson.annotation;

    requires transitive com.truthbean.debbie.core;
    requires org.slf4j;
    requires com.truthbean.logger.jcl;
    requires org.apache.logging.log4j;
    requires com.truthbean.debbie.asm;
}