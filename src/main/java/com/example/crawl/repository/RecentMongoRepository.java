package com.example.crawl.repository;

import com.example.crawl.vo.ArticleId;
import com.example.crawl.entities.RecentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RecentMongoRepository extends MongoRepository<RecentEntity, ArticleId> {

    public RecentEntity findByArticleId(ArticleId articleId);

    @Query("{'_id': {'$eq': ?0}}")
    public RecentEntity findRecentEntity(ArticleId articleId);


    @Query(value = "{$or: [{'isScrap': null}, {'isScrap': {'$exists': false}}]}", sort = "{'date': 1}")
    public List<RecentEntity> findIsNotScrap(Pageable pageable);

}
