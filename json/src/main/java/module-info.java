/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
module com.truthbean.debbie.json {
    exports com.truthbean.debbie.json;
    requires transitive com.truthbean.debbie.core;

    provides com.truthbean.debbie.data.JsonHelper
            with com.truthbean.debbie.json.DebbieJsonHelper;
}