package com.truthbean.debbie.check.annotation;

import com.truthbean.debbie.annotation.AnnotationInfo;
import com.truthbean.debbie.annotation.AnnotationParser;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.properties.PropertiesConfiguration;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
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
}