/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

import com.truthbean.debbie.netty.NettyServerApplication;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
module com.truthbean.debbie.netty {
    exports com.truthbean.debbie.netty;

    requires transitive com.truthbean.debbie.server;

    requires io.netty.transport;
    requires io.netty.codec.http;
    requires io.netty.buffer;
    requires io.netty.common;

    requires java.management;
    requires java.base;
    requires jdk.accessibility;
    requires jdk.unsupported;
    requires jdk.nio.mapmode;

    provides com.truthbean.debbie.boot.AbstractApplication with
            NettyServerApplication;

    provides com.truthbean.debbie.boot.DebbieModuleStarter with
            com.truthbean.debbie.netty.NettyModuleStarter;
}