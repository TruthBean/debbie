package com.truthbean.debbie.mvc.router;

/**
 * Functional interface for customising MVC router registration at
 * application startup.
 *
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/04 00:17.
 */
@FunctionalInterface
public interface CustomizeMvcRouterRegister {

    /**
     * Registers custom routers via the given {@link MvcRouterRegister}.
     *
     * @param mvcRouterRegister the router register
     */
    void registerMvcRegister(MvcRouterRegister mvcRouterRegister);
}
