/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
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
module com.truthbean.debbie.mvc {
    exports com.truthbean.debbie.mvc;
    exports com.truthbean.debbie.mvc.router;
    exports com.truthbean.debbie.mvc.response;
    exports com.truthbean.debbie.mvc.response.provider;
    exports com.truthbean.debbie.mvc.response.view;
    exports com.truthbean.debbie.mvc.csrf;
    exports com.truthbean.debbie.mvc.filter;
    exports com.truthbean.debbie.mvc.request;
    exports com.truthbean.debbie.mvc.url;

    opens com.truthbean.debbie.mvc.router to com.truthbean.common.mini, com.truthbean.debbie.core;
    opens com.truthbean.debbie.mvc.request to com.truthbean.common.mini, com.truthbean.debbie.core;

    requires transitive com.truthbean.debbie.core;

    uses com.truthbean.debbie.mvc.response.view.AbstractTemplateViewHandler;
    uses com.truthbean.debbie.mvc.router.RouterAnnotationParser;
    uses com.truthbean.debbie.mvc.request.RequestParameterParser;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.mvc.MvcModuleStarter;

    provides com.truthbean.debbie.reflection.ExecutableArgumentResolver with
            com.truthbean.debbie.mvc.request.RequestParameterResolver;
}