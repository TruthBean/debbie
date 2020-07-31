/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

import com.truthbean.debbie.undertow.UndertowServerApplication;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
module com.truthbean.debbie.undertow {
    exports com.truthbean.debbie.undertow to com.truthbean.debbie.core;
    requires transitive com.truthbean.debbie.server;
    requires transitive java.management;
    // not work
    requires transitive io.undertow.core;
    requires transitive org.xnio.api;

    provides com.truthbean.debbie.boot.AbstractApplication with
            UndertowServerApplication;

    provides com.truthbean.debbie.boot.DebbieModuleStarter with
            com.truthbean.debbie.undertow.UndertowModuleStarter;
}