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
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
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


    @Bean
    public JobDetail recentJobDetail()
    {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "recent");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);

        return JobBuilder.newJob(NewsCrawlJobBean.class)
                .withIdentity("recentJobDetail")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }



    @Bean
    public JobDetail articleJobDetail()
    {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "article");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);

        return JobBuilder.newJob(NewsCrawlJobBean.class)
                .withIdentity("articleJobDetail")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }



    @Bean
    public JobDetail sendToKafkaJobDetail()
    {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "kafka");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);

        return JobBuilder.newJob(NewsCrawlJobBean.class)
                .withIdentity("sendToKafkaJobDetail")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }





    @Bean
    public Trigger recentJobTrigger()
    {
        SimpleScheduleBuilder scheduleBuilder =
                SimpleScheduleBuilder
                    .simpleSchedule()
                    .withIntervalInSeconds(60 * 5)
                    .repeatForever();

        return TriggerBuilder
                .newTrigger()
                .forJob(recentJobDetail())
                .withIdentity("recentJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }


    @Bean
    public Trigger articleJobTrigger()
    {
        SimpleScheduleBuilder scheduleBuilder =
                SimpleScheduleBuilder
                    .simpleSchedule()
                    .withIntervalInMinutes(13)
                    .repeatForever();

        return TriggerBuilder
                .newTrigger()
                .forJob(articleJobDetail())
                .withIdentity("articleJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }


    @Bean
    public Trigger sendToKafkaJobTrigger()
    {
        SimpleScheduleBuilder scheduleBuilder =
                SimpleScheduleBuilder
                        .simpleSchedule()
                        .withIntervalInMinutes(1)
                        .repeatForever();

        return TriggerBuilder
                .newTrigger()
                .forJob(sendToKafkaJobDetail())
                .withIdentity("sendToKafkaJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }


    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException
    {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();

        scheduler.setJobDetails(recentJobDetail(), articleJobDetail(), sendToKafkaJobDetail());
        scheduler.setTriggers(recentJobTrigger(), articleJobTrigger(), sendToKafkaJobTrigger());

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
