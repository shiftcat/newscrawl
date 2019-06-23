package com.example.crawl.jobs.khan;

import com.example.crawl.common.Config;
import com.example.crawl.common.PageGen;
import com.example.crawl.entities.RecentEntity;
import com.example.crawl.parser.RecentParser;
import com.example.crawl.parser.khan.KhanRecentParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 경향신문 최신 기사 목록 스크랩
 */

@Slf4j
@Configuration
public class KhanRecentScrapConfiguration
{


    private final StepBuilderFactory stepBuilderFactory;

    private final ItemWriter<List<RecentEntity>> recentWriter;

    private final RecentParser recentParser;

    private final PageGen pageGen;



    public KhanRecentScrapConfiguration(
            StepBuilderFactory stepBuilderFactory,
            ItemWriter<List<RecentEntity>> recentWriter,
            PageGen pageGen,
            KhanRecentParser recentParser
    )
    {
        this.stepBuilderFactory = stepBuilderFactory;
        this.recentWriter = recentWriter;
        this.recentParser = recentParser;
        this.pageGen = pageGen;
    }




    private static final String RECENT_URL = "http://news.khan.co.kr/kh_recent/index.html?page=%d";



    @Bean
    public Step khanRecentScrapStep()
    {
        return stepBuilderFactory.get("khanRecentScrapStep")
                .<Document, List<RecentEntity>>chunk(1)
                .reader(recentListReader())
                .processor(recentParser())
                .writer(recentWriter)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        log.debug("===== khanRecentScrapStep ====");
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        log.debug("==== khanRecentScrapStep ====");
                        return ExitStatus.COMPLETED;
                    }
                })
                .build();
    }



    private ItemReader<Document> recentListReader()
    {
        return () -> {
            Integer page = pageGen.getPageNumber();
            String url = String.format(RECENT_URL, page);
            log.debug(url);
            if(page != null) {
                Document doc = Jsoup
                        .connect(String.format(RECENT_URL, page))
                        .userAgent(Config.USER_AGENT)
                        .get();
                return doc;
            }
            else {
                return null;
            }
        };
    }



    private ItemProcessor<Document, List<RecentEntity>> recentParser()
    {
        return doc -> {
            Elements elements = doc.select("div.news_list > ul > li");

            List<RecentEntity> recentEntities =
                    elements.stream()
                            .map(recentParser::parse)
                            .filter(e -> e != null)
                            .collect(Collectors.toList());

            return recentEntities;
        };
    }


}
