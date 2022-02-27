/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/03 12:10.
 */
module com.truthbean.debbie.asm {
    requires transitive com.truthbean.debbie.core;
    requires transitive org.objectweb.asm;

    exports com.truthbean.debbie.asm.proxy;
    exports com.truthbean.debbie.asm.reflect;

    provides com.truthbean.debbie.boot.DebbieModuleStarter
            with com.truthbean.debbie.asm.AsmModuleStarter;
}