package com.example.crawl.entities;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


/**
 * 뉴스 기사 엔티티
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@ToString
@Document(collection = "article")

public class ArticleEntity
{

    @Id
    private ArticleId articleId;

    private String subject;

    private String article;

    private String writer;

    private String cate;

    private String date;

    private String url;

    private List<ImgTag> images;

    private Integer sendToKafka;

}
