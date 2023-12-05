package com.truthbean.debbie.check.jdbc.repository;

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
                     @BeanInject DdlRepository ddlRepository) {
        System.out.println(configuration);
        System.out.println(mariadbConfiguration);
        System.out.println(h2Configuration);
        TransactionManager.offer(new TransactionInfo());
        System.out.println(ddlRepository.getTransaction());
    }

    @Test
    public synchronized void now(@BeanInject("mariadbDataSourceFactory") DataSourceFactory factory) {
        for (int i = 0; i < 1; i++) {
            TransactionManager.offer(factory.getTransaction());
            LocalDateTime localDateTime = dateTimeRepository.localDateTime();
            System.out.println("LocalDateTime");
            System.out.println(localDateTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss.S")));

            LocalDate localDate = dateTimeRepository.localDate();
            System.out.println("LocalDate");
            System.out.println(localDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));

            LocalTime localTime = dateTimeRepository.localTime();
            System.out.println("LocalTime");
            System.out.println(localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss.S")));

            Instant instant = dateTimeRepository.instant();
            System.out.println("Instant");
            System.out.println(instant.toString());

            Date date = dateTimeRepository.date();
            System.out.println("Date");
            System.out.println(date.toString());

            java.sql.Date sqlDate = dateTimeRepository.sqlDate();
            System.out.println("java.sql.Date");
            System.out.println(sqlDate);

            Time time = dateTimeRepository.sqlTime();
            System.out.println("Time");
            System.out.println(time);

            Timestamp timestamp = dateTimeRepository.timestamp();
            System.out.println("Timestamp");
            System.out.println(timestamp);

            Calendar calendar = dateTimeRepository.calendar();
            System.out.println("Calendar");
            System.out.println(calendar);

            Long now = dateTimeRepository.now();
            System.out.println("Long");
            System.out.println(now);

            DateTimeEntity dateTimeEntity = dateTimeRepository.dateTimeEntity();
            System.out.println("Long");
            System.out.println(dateTimeEntity);
        }
    }
}