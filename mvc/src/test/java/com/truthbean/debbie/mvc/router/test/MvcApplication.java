/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router.test;

import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;

import java.security.Permission;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-11-27 15:59
 */
@DebbieBootApplication
public class MvcApplication {

    /*static {
        try {
            System.setProperty(LoggerFactory.STD_OUT, "true");
            System.setSecurityManager(new NoReflectionSecurityManager());
        } catch (SecurityException se) {
            System.out.println("SecurityManager already set!");
        }
    }*/

    public static void main(String[] args) {
        DebbieApplication application = DebbieApplication.create(MvcApplication.class, args);
        ApplicationContext applicationContext = application.getApplicationContext();
        MvcConfiguration mvcConfiguration = applicationContext.factory(MvcConfiguration.class);
        MvcRouterRegister.getInstance(mvcConfiguration)
                .get(new String[]{"/register/get/router"}, (request, response) -> {

                    // response.setResponseType(MediaType.TEXT_ANY_UTF8);
                    response.setContent("hello router!");
                });
        application.start();
        // application.exit();
    }
}
