package test;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/28 15:32.
 */
@DebbieBootApplication(scan = @DebbieScan(basePackages = {"test", "com.truthbean.debbie.check"}))
public class BeanNativeScanTest {

    public static void main(String[] args) {
        ApplicationFactory factory = ApplicationFactory.configure(BeanNativeScanTest.class, args);
        ApplicationContext context = factory.getApplicationContext();
        BeanInfoManager manager = context.getBeanInfoManager();
        manager.printGraalvmConfig(context);
    }
}
