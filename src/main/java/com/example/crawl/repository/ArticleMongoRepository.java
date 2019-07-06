package com.example.crawl.repository;

import com.example.crawl.entities.ArticleEntity;
import com.example.crawl.vo.ArticleId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ArticleMongoRepository extends MongoRepository<ArticleEntity, ArticleId> {

    public ArticleEntity findByArticleId(ArticleId articleId);

    public List<ArticleEntity> findBySendToKafkaIsNull(Pageable pageable);

}
