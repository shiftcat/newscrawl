package com.example.crawl.entities;


import com.example.crawl.vo.ArticleId;
import com.example.crawl.vo.ImgTag;
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

    private String content;

    private String writer;

    private String cate;

    private String artiDate;

    private String artiTime;

    private String requestUrl;

    private String responseUrl;

    private List<ImgTag> images;

    private Integer sendToKafka;

    private boolean updated;

}
