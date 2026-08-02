package com.truthbean.debbie.check.jdbc.repository;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.check.jdbc.entity.DateTimeEntity;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.repository.DdlRepository;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author truthbean
 * @since Created on 2020/7/11 14:34.
 */
@DebbieApplicationTest(scan = @DebbieScan(basePackages = "com.truthbean.debbie"))
public class DateTimeRepositoryTest {

    static {
        System.setProperty("logging.level.com.truthbean.debbie", "trace");
    }

    private DateTimeRepository dateTimeRepository;

    @BeanInject
    public void setDateTimeRepository(DateTimeRepository dateTimeRepository) {
        this.dateTimeRepository = dateTimeRepository;
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void test(@BeanInject(name = "dataSourceConfiguration") DataSourceConfiguration configuration,
                     @BeanInject(category = "mariadb") DataSourceConfiguration mariadbConfiguration,
                     @BeanInject(category = "h2") DataSourceConfiguration h2Configuration,
                     @BeanInject(category = "gauss") DataSourceConfiguration gaussConfiguration,
                     @BeanInject DdlRepository ddlRepository) {
        Console.println(configuration);
        Console.println(mariadbConfiguration);
        Console.println(h2Configuration);
        Console.println(gaussConfiguration);
        TransactionManager.offer(new TransactionInfo());
        Console.println(ddlRepository.getTransaction());
    }

    @Test
    public synchronized void now(@BeanInject("mariadbDataSourceFactory") DataSourceFactory factory) {
        for (int i = 0; i < 1; i++) {
            TransactionManager.offer(factory.getTransaction());
            LocalDateTime localDateTime = dateTimeRepository.localDateTime();
            Console.println("LocalDateTime");
            Console.println(localDateTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss.S")));

            LocalDate localDate = dateTimeRepository.localDate();
            Console.println("LocalDate");
            Console.println(localDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));

            LocalTime localTime = dateTimeRepository.localTime();
            Console.println("LocalTime");
            Console.println(localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss.S")));

            Instant instant = dateTimeRepository.instant();
            Console.println("Instant");
            Console.println(instant.toString());

            Date date = dateTimeRepository.date();
            Console.println("Date");
            Console.println(date.toString());

            java.sql.Date sqlDate = dateTimeRepository.sqlDate();
            Console.println("java.sql.Date");
            Console.println(sqlDate);

            Time time = dateTimeRepository.sqlTime();
            Console.println("Time");
            Console.println(time);

            Timestamp timestamp = dateTimeRepository.timestamp();
            Console.println("Timestamp");
            Console.println(timestamp);

            Calendar calendar = dateTimeRepository.calendar();
            Console.println("Calendar");
            Console.println(calendar);

            Long now = dateTimeRepository.now();
            Console.println("Long");
            Console.println(now);

            DateTimeEntity dateTimeEntity = dateTimeRepository.dateTimeEntity();
            Console.println("Long");
            Console.println(dateTimeEntity);
        }
    }
}