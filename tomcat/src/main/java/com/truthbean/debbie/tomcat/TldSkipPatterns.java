package com.truthbean.debbie.tomcat;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-02-27 15:31
 */
class TldSkipPatterns {
    private static StringBuilder stringBuilder = new StringBuilder();

    static {
        stringBuilder
            .append("ant-*.jar").append(",")
            .append("aspectj*.jar").append(",")
            .append("commons-beanutils*.jar").append(",")
            .append("commons-codec*.jar").append(",")
            .append("commons-collections*.jar").append(",")
            .append("commons-dbcp*.jar").append(",")
            .append("commons-digester*.jar").append(",")
            .append("commons-fileupload*.jar").append(",")
            .append("commons-httpclient*.jar").append(",")
            .append("commons-io*.jar").append(",")
            .append("commons-lang*.jar").append(",")
            .append("commons-logging*.jar").append(",")
            .append("commons-math*.jar").append(",")
            .append("commons-pool*.jar").append(",")
            .append("geronimo-spec-jaxrpc*.jar").append(",")
            .append("h2*.jar").append(",")
            .append("hamcrest*.jar").append(",")
            .append("hibernate*.jar").append(",")
            .append("jaxb-runtime-*.jar").append(",")
            .append("jmx*.jar").append(",")
            .append("jmx-tools-*.jar").append(",")
            .append("jta*.jar").append(",")
            .append("junit-*.jar").append(",")
            .append("httpclient*.jar").append(",")
            .append("log4j-*.jar").append(",")
            .append("mail*.jar").append(",")
            .append("org.hamcrest*.jar").append(",")
            .append("slf4j*.jar").append(",")
            .append("tomcat-embed-core-*.jar").append(",")
            .append("tomcat-embed-logging-*.jar").append(",")
            .append("tomcat-jdbc-*.jar").append(",")
            .append("tomcat-juli-*.jar").append(",")
            .append("tomcat-embed-el-*.jar").append(",")
            .append("tools.jar").append(",")
            .append("wsdl4j*.jar").append(",")
            .append("xercesImpl*.jar").append(",")
            .append("xmlParserAPIs*.jar").append(",")
            .append("serializer*.jar").append(",")
            .append("xml-apis*.jar");

        stringBuilder.append(",").append("xercesImpl.jar")
            .append(",").append("xml-apis.jar")
            .append(",").append("serializer.jar");

        stringBuilder.append(",")
            .append("cglib-*.jar").append(",")
            .append("antlr-*.jar").append(",")
            .append("aopalliance-*.jar").append(",")
            .append("aspectjrt-*.jar").append(",")
            .append("aspectjweaver-*.jar").append(",")
            .append("classmate-*.jar").append(",")
            .append("dom4j-*.jar").append(",")
            .append("ecj-*.jar").append(",")
            .append("aopalliance-*.jar").append(",")
            .append("hibernate-core-*.jar").append(",")
            .append("jackson-annotations-*.jar").append(",")
            .append("jackson-core-*.jar").append(",")
            .append("jackson-databind-*.jar").append(",")
            .append("jackson-dataformat-xml-*.jar").append(",")
            .append("jackson-dataformat-yaml-*.jar").append(",")
            .append("jackson-module-jaxb-annotations-*.jar").append(",")
            .append("woodstox-core-*.jar").append(",")
        ;
    }

    public static String tldSkipPatterns() {
        return stringBuilder.toString();
    }
}
