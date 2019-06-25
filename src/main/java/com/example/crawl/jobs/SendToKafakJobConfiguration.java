package com.example.crawl.jobs;


import com.example.crawl.entities.ArticleEntity;
import com.example.crawl.repository.ArticleMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class SendToKafakJobConfiguration
{

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final ArticleMongoRepository articleMongoRepository;

    private final KafkaTemplate kafkaTemplate;


    @Bean
    public Job sendToKafkaJob()
    {
        return jobBuilderFactory.get("kafka")
                .start(sendToKafkaStep())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("===== Send to kafka job begin ====");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("===== Send to kafka job end ====");
                    }
                })
                .build();
    }


    @Bean
    public Step sendToKafkaStep()
    {
        return stepBuilderFactory.get("sendToKafkaStep")
                .tasklet((contribution, chunkContext) -> {
                    sendToKafka();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }


    private void sendToKafka()
    {
        Pageable pageable = new PageRequest(0, 5);

        List<ArticleEntity> articleEntities =
                articleMongoRepository.findBySendToKafkaIsNull(pageable);

        articleEntities.forEach( entity -> {
            try {
                kafkaTemplate.send("newscrawl", entity);
                entity.setSendToKafka(1);
                articleMongoRepository.save(entity);
            }
            catch (Exception e) {
                entity.setSendToKafka(-1);
                articleMongoRepository.save(entity);
            }
        });
    }


}
