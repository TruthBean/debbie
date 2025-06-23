package com.truthbean.debbie.check.annotation;

import com.truthbean.debbie.annotation.AliasFor;
import com.truthbean.debbie.annotation.AnnotationInfo;
import com.truthbean.debbie.annotation.AnnotationParser;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-11-25 15:31
 */
class AnnotationParserTest {

    static {
        System.setProperty(DebbieApplication.DISABLE_DEBBIE, "true");
    }

    @Test
    void parse() {
        var clazz = AnnotationContainerTest.PropertiesConfigurationTest.class;
        var annotation = clazz.getAnnotation(PropertiesConfiguration.class);
        var annotationInfo = AnnotationParser.parse(annotation);
        System.out.println(annotationInfo);
        System.out.println(annotationInfo.getOrigin());
        System.out.println();
        annotationInfo.properties().forEach((name, info) -> {
            System.out.println(name);
            System.out.println(info.getValue());
            System.out.println(info);
        });
        System.out.println();
        System.out.println("------------------------------------------------------");

        Map<Class<? extends Annotation>, AnnotationInfo> map = AnnotationParser.parseClassAnnotation(clazz);
        map.forEach((type, info) -> {
            System.out.println("---------");
            System.out.println(type);
            System.out.println(info.getOrigin());
            info.properties().forEach((name, property) -> {
                System.out.println();
                System.out.println(name);
                System.out.println(property.getValue());
            });
        });

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface TestAnnotation {
        String value() default "";
        String name() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface AliasAnnotation {
        @AliasFor(annotation = TestAnnotation.class, attribute = "value")
        String aliasValue() default "";
    }

    @TestAnnotation(value = "testValue", name = "testName")
    static class TestClass {}

    @AliasAnnotation(aliasValue = "aliasTest")
    static class AliasTestClass {}

    @Test
    void testParse() throws Exception {
        TestAnnotation annotation = TestClass.class.getAnnotation(TestAnnotation.class);
        AnnotationInfo info = AnnotationParser.parse(annotation);

        assertNotNull(info);
        assertEquals(2, info.properties().size());
        assertTrue(info.properties().containsKey("value"));
        assertEquals("testValue", info.properties().get("value").getValue());
    }

    @Test
    void testParseWithAlias() throws Exception {
        AliasAnnotation annotation = AliasTestClass.class.getAnnotation(AliasAnnotation.class);
        AnnotationInfo info = AnnotationParser.parse(annotation);

        assertNotNull(info);
        assertEquals(1, info.properties().size());
        assertTrue(info.properties().get("aliasValue").hasAliasFor());
        assertEquals("value", info.properties().get("aliasValue").getAliasForAttribute());
    }

    @Test
    void testParseClassAnnotation() {
        Map<Class<? extends Annotation>, AnnotationInfo> result =
                AnnotationParser.parseClassAnnotation(TestClass.class);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(TestAnnotation.class));
    }

    @Test
    void testParseClassAnnotationWithAlias() {
        Map<Class<? extends Annotation>, AnnotationInfo> result =
                AnnotationParser.parseClassAnnotation(AliasTestClass.class);

        assertNotNull(result);
        /*assertEquals(2, result.size());
        assertTrue(result.containsKey(AliasAnnotation.class));
        assertTrue(result.containsKey(TestAnnotation.class));*/

        assertEquals(1, result.size());
        assertTrue(result.containsKey(AliasAnnotation.class));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface EmptyAnnotation {}

    @EmptyAnnotation
    class EmptyClass {}

    @Test
    void testParseEmptyAnnotation() {
        Map<Class<? extends Annotation>, AnnotationInfo> result =
                AnnotationParser.parseClassAnnotation(EmptyClass.class);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface NestedAnnotation {
        TestAnnotation value();
    }

    @NestedAnnotation(@TestAnnotation("nested"))
    class NestedClass {}

    @Test
    void testParseClassAnnotationWithNested() {
        Map<Class<? extends Annotation>, AnnotationInfo> result =
                AnnotationParser.parseClassAnnotation(NestedClass.class);

        assertNotNull(result);
        /*assertEquals(2, result.size());
        assertTrue(result.containsKey(NestedAnnotation.class));
        assertTrue(result.containsKey(TestAnnotation.class));*/

        assertEquals(1, result.size());
        assertTrue(result.containsKey(NestedAnnotation.class));
    }
}