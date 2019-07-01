package com.example.crawl.jobs;

import com.example.crawl.common.Config;
import com.example.crawl.entities.ArticleEntity;
import com.example.crawl.entities.ArticleId;
import com.example.crawl.entities.ArticleResponse;
import com.example.crawl.entities.RecentEntity;
import com.example.crawl.parser.ArticleParser;
import com.example.crawl.parser.ParserSelector;
import com.example.crawl.repository.ArticleMongoRepository;
import com.example.crawl.repository.RecentMongoRepository;
import com.google.common.base.Throwables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.net.URL;
import java.util.List;



/**
 * 뉴스 기사 수집을 배치 Job 설정 클래스
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ArticleScrapJobConfiguration
{

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final RecentMongoRepository recentMongoRepository;

    private final ArticleMongoRepository articleMongoRepository;

    private final ParserSelector parserSelector;



    @Bean
    public Job articleScrapJob(@Qualifier("articleScrapStep") Step articleScrapStep)
    {
        return jobBuilderFactory.get("article")
                .start(articleScrapStep)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("===== Article scrap job begin ====");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("===== Article scrap job end ====");
                    }
                })
                .build();
    }




    @Bean
    public Step articleScrapStep()
    {
        return stepBuilderFactory.get("articleScrapStep")
                .tasklet((contribution, chunkContext) -> {
                    scrapArticle();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }




    private void randomDelay(float min, float max)
    {
        int random = (int)(max * Math.random() + min);
        try {
            Thread.sleep(random * 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    public void scrapArticle()
    {
        Pageable pageable = new PageRequest(0, Config.ARTICLE_SCRAP_SIZE);

        List<RecentEntity> recentEntities = recentMongoRepository.findIsNotScrap(pageable);
        recentEntities.stream()
                .forEach(e -> {
                    log.debug("Request responseUrl : " + e.getLink());
                    log.debug("Read recent data : " + e.toString());

                    // Read
                    ArticleResponse response = null;
                    try {
                        response = readArticle(e.getLink(), e.getArticleId());
                    } catch (IOException e1) {
                        log.error(Throwables.getStackTraceAsString(e1));
                        e.setIsScrap(-2);
                        updateRecent(e);
                    }

                    if(response != null) {
                        ArticleEntity articleEntity = null;
                        String url = response.getUrl().toString();

                        // Process
                        ArticleParser parser = parserSelector.select(url);
                        if(parser != null) {
                            articleEntity = parser.parse(response.getDocument());
                        }

                        // Write
                        if( articleEntity != null ) {
                            articleEntity.setRequestUrl(e.getLink());
                            articleEntity.setResponseUrl(url);
                            saveArticle(e, articleEntity);
                        }
                        else {
                            e.setIsScrap(-1);
                            updateRecent(e);
                        }
                    }

                    randomDelay(0.3f, 1.9f);
                });
    }



    private ArticleResponse readArticle(String url, ArticleId articleId) throws IOException
    {
        log.debug("Read article => " + articleId);

        ArticleEntity existsArticle =
                articleMongoRepository.findByArticleId(articleId);

        ArticleResponse rtnVal = null;

        log.debug("Exists article => " + existsArticle);

        if(existsArticle == null) {
            Connection connect = Jsoup
                    .connect(url)
                    .userAgent(Config.USER_AGENT);

            Connection.Response response = connect.execute();

            URL realUrl = response.url();

            log.debug("Response responseUrl : " + realUrl);

            Document doc = response.parse();

            rtnVal = new ArticleResponse();
            rtnVal.setUrl(realUrl);
            rtnVal.setDocument(doc);
        }

        return rtnVal;
    }





    private void saveArticle(RecentEntity recentEntity, ArticleEntity articleEntity)
    {
        recentEntity.setIsScrap(1);
        recentMongoRepository.save(recentEntity);

        if(recentEntity.getCate() != null) {
            articleEntity.setCate(recentEntity.getCate());
        }
        articleEntity.setWriter(recentEntity.getWriter());
        articleEntity.setArticleId(recentEntity.getArticleId());
        articleEntity.setDate(recentEntity.getDate());
        articleMongoRepository.save(articleEntity);
    }


    private void updateRecent(RecentEntity entity)
    {
        recentMongoRepository.save(entity);
    }


}
