package com.example.crawl.jobs.chosun;


import com.example.crawl.common.Config;
import com.example.crawl.common.PageGen;
import com.example.crawl.entities.RecentEntity;
import com.example.crawl.parser.RecentParser;
import com.example.crawl.parser.chosun.ChosunParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 조선일보 최신 기사 목록 스크랩
 *
 */

@Slf4j
@Configuration
public class ChosunRecentScrapConfiguration
{


    private final StepBuilderFactory stepBuilderFactory;

    private final ItemWriter<List<RecentEntity>> recentWriter;

    private final RecentParser parser;

    private final PageGen pageGen;


    public ChosunRecentScrapConfiguration(
            StepBuilderFactory stepBuilderFactory,
            ItemWriter<List<RecentEntity>> recentWriter,
            PageGen pageGen,
            ChosunParser parser
    ) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.recentWriter = recentWriter;
        this.parser = parser;
        this.pageGen = pageGen;
    }



    private static final String RECENT_URL = "http://news.chosun.com/svc/list_in/list.html?pn=%d";



    @Bean
    public Step chosunRecentScrapStep()
    {
        return stepBuilderFactory.get("chosunRecentScrapStep")
                .<Document, List<RecentEntity>>chunk(1)
                .reader(recentListReader())
                .processor(recentParser())
                .writer(recentWriter)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        log.debug("===== ChosunRecentScrapStep begin ====");
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        log.debug("==== ChosunRecentScrapStep end ====");
                        return ExitStatus.COMPLETED;
                    }
                })
                .build();
    }


    @Bean
    public Flow chosunRecentScrapFlow()
    {
        return new FlowBuilder<Flow>("chosunRecentScrapFlow")
                .start(chosunRecentScrapStep())
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
                        .connect(url)
                        .timeout(Config.CONNECTION_TIME_OUT)
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
            Elements elements = doc.select("div.list_content > dl.list_item");
            List<RecentEntity> recentEntities =
                    elements.stream()
                            .map(parser::parse)
                            .filter(e -> e.getArticleId() != null)
                            .collect(Collectors.toList());

            return recentEntities;
        };
    }


}
