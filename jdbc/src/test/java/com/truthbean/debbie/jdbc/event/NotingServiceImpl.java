package com.truthbean.debbie.jdbc.event;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanType;

import java.util.List;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-01-23 16:45
 */
@BeanComponent(value = "notingService", lazy = true, type = BeanType.SINGLETON)
public class NotingServiceImpl implements NotingService<Void, Void> {
    @Override
    public List<Void> listAll() {
        return null;
    }
}
