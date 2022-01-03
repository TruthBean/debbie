package com.truthbean.debbie.jdbc.entity;

import com.truthbean.debbie.bean.Aware;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/23 20:44.
 */
public interface EntityResolverAware extends Aware {

    void setEntityResolver(EntityResolver entityResolver);
}
