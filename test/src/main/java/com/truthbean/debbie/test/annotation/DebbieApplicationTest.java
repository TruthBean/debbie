/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.test.annotation;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020/7/9 15:57.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent
@DebbieBootApplication
@ExtendWith({DebbieApplicationExtension.class})
public @interface DebbieApplicationTest {
    /**
     * event bean name
     * @return router bean name
     */
    @BeanAliceForValue
    @BeanAliceFor(name = "value")
    String value() default "";

    /**
     * event bean name
     * @return router bean name
     */
    @BeanAliceForName
    @BeanAliceFor(name = "name")
    String name() default "";

     Class<? extends Annotation>[] customInjectType() default {};

     DebbieScan scan() default @DebbieScan();
}
