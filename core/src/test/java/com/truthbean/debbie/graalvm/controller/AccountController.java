package com.truthbean.debbie.graalvm.controller;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.graalvm.model.Account;
import com.truthbean.debbie.graalvm.service.AccountService;
import com.truthbean.debbie.jackson.util.JacksonUtils;
import com.truthbean.debbie.mvc.router.CustomizeMvcRouterRegister;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/26 12:05.
 */
public class AccountController implements ApplicationContextAware, CustomizeMvcRouterRegister {

    private AccountService accountService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        accountService = applicationContext.factory(AccountService.class);
    }

    @Override
    public void registerMvcRegister(MvcRouterRegister mvcRouterRegister) {
        mvcRouterRegister.get("/account/{id}", (request, response) -> {
            Long id = request.getPathAttributeValue("id", Long.class);
            Account account = getAccountInfo(id);
            response.setContent(JacksonUtils.toJson(account));
        });
    }

    public Account getAccountInfo(Long id) {
        return accountService.query(id);
    }
}
