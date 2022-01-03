package com.truthbean.debbie.jdbc.entity;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/13 20:40.
 */
@FunctionalInterface
public interface EntityPropertyGetter<Entity, Property> {

    Property get(Entity entity);
}
