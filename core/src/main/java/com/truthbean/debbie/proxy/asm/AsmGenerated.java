package com.truthbean.debbie.proxy.asm;

import java.lang.annotation.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-26 23:17
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsmGenerated {
}
