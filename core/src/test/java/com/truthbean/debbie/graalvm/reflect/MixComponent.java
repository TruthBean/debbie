package com.truthbean.debbie.graalvm.reflect;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.graalvm.ApplicationEntrypoint;
import com.truthbean.debbie.proxy.BeanProxyType;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/06 10:04.
 */
// @BeanComponent(type = BeanType.SINGLETON, proxy = BeanProxyType.NO)
public class MixComponent {

    @BeanInject
    private ApplicationEntrypoint applicationEntrypoint;

    @Override
    public String toString() {
        return "\"MixComponent\":{" + "\"applicationEntrypoint\":" + applicationEntrypoint + '}';
    }
}
