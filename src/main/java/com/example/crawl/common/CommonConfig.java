package com.example.crawl.common;


import com.example.crawl.entities.RecentEntity;
import com.example.crawl.repository.RecentMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class CommonConfig
{

    private final RecentMongoRepository recentMongoRepository;



    private void randomDelay(float min, float max)
    {
        int random = (int)(max * Math.random() + min);
        try {
            Thread.sleep(random * 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @Bean
    @StepScope
    public ItemWriter<List<RecentEntity>> recentWriter()
    {
        return recentList -> {
            recentList.forEach(recentEntitis -> {
                recentEntitis.forEach(recent -> {
                    RecentEntity existsRecent =
                            recentMongoRepository.findRecentEntity(recent.getArticleId());
                    if(existsRecent == null) {
                        log.debug(recent.toString());
                        recentMongoRepository.save(recent);
                    }
                });
            });

            randomDelay(1.0f, 3.0f);
        };
    }



}
