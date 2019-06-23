package com.example.crawl.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * 최신 기사 목록 데이터 엔티티
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@ToString
@Document(collection = "recent")
public class RecentEntity
{
    @Id
    private ArticleId articleId;

    private String subject;

    private String cate;

    private String link;

    private String writer;

    private String date;

    private Integer isScrap;

}
