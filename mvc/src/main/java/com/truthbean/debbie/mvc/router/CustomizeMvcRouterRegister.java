package com.truthbean.debbie.mvc.router;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/04 00:17.
 */
@FunctionalInterface
public interface CustomizeMvcRouterRegister {

    void registerMvcRegister(MvcRouterRegister mvcRouterRegister);
}
