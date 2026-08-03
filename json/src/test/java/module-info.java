/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
open module com.truthbean.debbie.json.test {
    exports com.truthbean.debbie.check.json;

    requires java.base;
    requires transitive com.truthbean.debbie.json;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;
}