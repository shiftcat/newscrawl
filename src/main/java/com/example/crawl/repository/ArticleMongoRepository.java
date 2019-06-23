package com.example.crawl.repository;

import com.example.crawl.entities.ArticleEntity;
import com.example.crawl.entities.ArticleId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleMongoRepository extends MongoRepository<ArticleEntity, ArticleId> {

    public ArticleEntity findByArticleId(ArticleId articleId);

}
