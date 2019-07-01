package com.example.crawl.queartz;

import com.google.common.base.Throwables;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
@Setter
@Getter
public class NewsCrawlJobBean extends QuartzJobBean
{

    private String jobName;

    private JobLauncher jobLauncher;

    private JobLocator jobLocator;


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException
    {
        try {
            Job job = jobLocator.getJob(jobName);
            JobParameters params =
                    new JobParametersBuilder()
                            .addString("JobID", String.valueOf(System.currentTimeMillis()))
                            .toJobParameters();

            jobLauncher.run(job, params);
        }
        catch (Exception e) {
            log.error(Throwables.getStackTraceAsString(e));
        }
    }
}
