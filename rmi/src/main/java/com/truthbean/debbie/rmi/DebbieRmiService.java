package com.truthbean.debbie.rmi;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DebbieRmiService {
}
