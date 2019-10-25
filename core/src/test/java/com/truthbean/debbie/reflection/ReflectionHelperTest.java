package com.truthbean.debbie.reflection;

import org.junit.jupiter.api.Test;

public class ReflectionHelperTest {

    @Test
    public void test() {
        ReflectionHelper.newInstance("com.truthbean.debbie.reflection.ReflectionTarget");
    }
}
