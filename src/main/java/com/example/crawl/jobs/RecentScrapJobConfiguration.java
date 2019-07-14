package com.example.crawl.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;


/**
 * 최시 기가 목록 수집을 위하 배치 Job 설정 클래스
 */

@Slf4j
@RequiredArgsConstructor
@Configuration
public class RecentScrapJobConfiguration
{

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;


//    private final Step khanRecentScrapStep;
//
//    private final Step chosunRecentScrapStep;

    private final Flow khanRecentScrapFlow;

    private final Flow chosunRecentScrapFlow;


//    @Bean
//    public Job recentScrapJob()
//    {
//        return jobBuilderFactory.get("recent")
//                .start(dumyStep())
//                .next(khanRecentScrapStep)
//                .next(chosunRecentScrapStep)
//                .listener(new JobExecutionListener() {
//                    @Override
//                    public void beforeJob(JobExecution jobExecution) {
//                        log.info("===== Recent scrap job begin ====");
//                    }
//
//                    @Override
//                    public void afterJob(JobExecution jobExecution) {
//                        log.info("===== Recent scrap job end ====");
//                    }
//                })
//                .build();
//    }


    @Bean
    public Job recentScrapJob()
    {
        return jobBuilderFactory.get("recent")
                .start(dumyStep())
                .split(new SimpleAsyncTaskExecutor())
                .add(khanRecentScrapFlow, chosunRecentScrapFlow)
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("===== Recent scrap job begin ====");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("===== Recent scrap job end ====");
                    }
                })
                .build();
    }



    private Step dumyStep()
    {
        return stepBuilderFactory.get("dumyStep")
                .tasklet((contribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                })
                .build();
    }




}
