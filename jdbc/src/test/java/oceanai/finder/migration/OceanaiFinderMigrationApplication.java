package oceanai.finder.migration;

import com.truthbean.debbie.bean.DebbieReflectionBeanFactory;
import com.truthbean.debbie.bean.SimpleBeanFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.ApplicationFactory;
import oceanai.finder.migration.service.OceanaiDatabaseHandler;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/09 16:24.
 */
public class OceanaiFinderMigrationApplication {

    static {
        System.setProperty("logging.level.com.truthbean", "trace");
    }

    public static void main(String[] args) {
        DebbieApplication factory = ApplicationFactory.newEmpty()
                .preInit(args)
                .init(OceanaiFinderMigrationApplication.class)
                /*.register(new SimpleBeanFactory<>(new AccountServiceImpl(), AccountService.class))
                .register(new SimpleBeanFactory<>(new LogService()))
                .register(new SimpleBeanFactory<>(new DebbieReadyEventListener()))
                .register(new SimpleBeanFactory<>(new AccountController(), AccountController.class))
                .register(new DebbieReflectionBeanFactory<>(AImpl.class))
                .register(new DebbieReflectionBeanFactory<>(AbcImpl.class))
                .register(new DebbieReflectionBeanFactory<>(BImpl.class))
                .register(new DebbieReflectionBeanFactory<>(CImpl.class))
                .register(new DebbieReflectionBeanFactory<>(DemoBeanComponent.class))
                .register(new DebbieReflectionBeanFactory<>(DemoBeanComponent.Demo2.class))
                .register(new DebbieReflectionBeanFactory<>(LifecycleBeanTest.class))
                .register(new DebbieReflectionBeanFactory<>(MixComponent.class))*/
                .config()
                .create()
                .postCreate()
                .build()
                .factory();
        factory.start();
        // OceanaiDatabaseHandler handler = new OceanaiDatabaseHandler();
        // handler.loadSql();
        LocalDateTime time = LocalDateTime.now();
        Locale locale = Locale.CHINA;
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTimeInMillis(time.toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        System.out.println(timestamp);
        System.out.println(timestamp.getTime() - 86400 * 1000L);
        timestamp = new Timestamp(timestamp.getTime() - 86400 * 1000L);
        System.out.println(timestamp);
        factory.exit();
    }
}
