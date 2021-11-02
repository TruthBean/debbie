package com.truthbean.debbie.env;

/**
 * @author TruthBean
 * @since 0.5.3
 */
public interface ResourceEnvironmentContent extends MutableEnvironmentContent {

    void load(String resourceUri);

    void loadResource();
}
