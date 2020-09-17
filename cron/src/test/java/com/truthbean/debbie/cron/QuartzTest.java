package com.truthbean.debbie.cron;

import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

@DebbieApplicationTest
public class QuartzTest {

    @Test
    public void context() throws InterruptedException {
        Thread.sleep(10000);
    }

    public static void main(String[] args) {

        try {
            BaseProperties properties = new BaseProperties();
            properties.addProperty("org.quartz.threadPool.threadCount", "10");
            StdSchedulerFactory factory = new StdSchedulerFactory(properties.getProperties());
            Scheduler scheduler = factory.getScheduler();

            // define the job and tie it to our HelloJob class
            JobDetail job = newJob(HelloJob.class)
                    .withIdentity("job1", "group1")
                    .build();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(40)
                            .repeatForever())
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);

            // and start it off
            scheduler.start();

            // scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }
}