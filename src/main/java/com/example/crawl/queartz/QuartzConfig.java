package com.example.crawl.queartz;

import org.quartz.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

@Configuration
public class QuartzConfig
{
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobLocator jobLocator;


    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry)
    {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }


    private JobDataMap getJobDataMap(String jobName)
    {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", jobName);
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);

        return jobDataMap;
    }



    private ScheduleBuilder getScheduleBuilder(int interval)
    {
        return SimpleScheduleBuilder
                    .simpleSchedule()
                    .withIntervalInMinutes(interval)
                    .repeatForever();
    }



    @Bean
    @Profile("crawl")
    public JobDetail recentJobDetail()
    {
        JobDataMap jobDataMap = getJobDataMap("recent");

        return JobBuilder.newJob(NewsCrawlJobBean.class)
                .withIdentity("recentJobDetail")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }



    @Bean
    @Profile("crawl")
    public JobDetail articleJobDetail()
    {
        JobDataMap jobDataMap = getJobDataMap("article");

        return JobBuilder.newJob(NewsCrawlJobBean.class)
                .withIdentity("articleJobDetail")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }



    @Bean
    @Profile("kafka")
    public JobDetail sendToKafkaJobDetail()
    {
        JobDataMap jobDataMap = getJobDataMap("kafka");

        return JobBuilder.newJob(NewsCrawlJobBean.class)
                .withIdentity("sendToKafkaJobDetail")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }





    @Bean
    @Profile("crawl")
    public Trigger recentJobTrigger()
    {
        ScheduleBuilder scheduleBuilder
                = getScheduleBuilder(30);

        return TriggerBuilder
                .newTrigger()
                .forJob(recentJobDetail())
                .withIdentity("recentJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }


    @Bean
    @Profile("crawl")
    public Trigger articleJobTrigger()
    {
        ScheduleBuilder scheduleBuilder
                = getScheduleBuilder(3);

        return TriggerBuilder
                .newTrigger()
                .startAt(new Date(System.currentTimeMillis()+1000*60))
                .forJob(articleJobDetail())
                .withIdentity("articleJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }


    @Bean
    @Profile("kafka")
    public Trigger sendToKafkaJobTrigger()
    {
        ScheduleBuilder scheduleBuilder =
                SimpleScheduleBuilder
                        .simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever();

        return TriggerBuilder
                .newTrigger()
                .forJob(sendToKafkaJobDetail())
                .withIdentity("sendToKafkaJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }


    @Bean
    @Profile("crawl")
    public SchedulerFactoryBean crawlSchedulerFactoryBean() throws IOException
    {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();

        scheduler.setJobDetails(recentJobDetail(), articleJobDetail());
        scheduler.setTriggers(recentJobTrigger(), articleJobTrigger());

        scheduler.setQuartzProperties(quartzProperties());

        return scheduler;
    }


    @Bean
    @Profile("kafka")
    public SchedulerFactoryBean kafkaSchedulerFactoryBean() throws IOException
    {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();

        scheduler.setJobDetails(sendToKafkaJobDetail());
        scheduler.setTriggers(sendToKafkaJobTrigger());

        scheduler.setQuartzProperties(quartzProperties());

        return scheduler;
    }



    @Bean
    public Properties quartzProperties() throws IOException
    {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }


}
