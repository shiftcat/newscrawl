package com.example.crawl.parser.khan;

import com.example.crawl.entities.ArticleEntity;
import com.example.crawl.vo.Byline;
import com.example.crawl.vo.ImgTag;
import com.example.crawl.parser.ArticleParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class KhanNewsParser implements ArticleParser
{

    public ArticleEntity parse(Document node)
    {
        // .//div[contains(@class, 'art_header')]/div[@class='subject']/h1
        String subject = node.select("div.art_header > div.subject > h1").text();

        // .//div[@class='art_body']/p[@class='content_text']
        String content = node.select("div.art_body > p.content_text").text();

        // div[contains(@class, 'art_header')]/div[@class='subject']/span/a
        String writer = node.select("div.art_header > div.subject > span > a").text();

        Byline byline = KhanParserUtil.parserByline(node);

        List<ImgTag> imgs = KhanParserUtil.parserImgTag(node);

        ArticleEntity article = new ArticleEntity();
        article.setSubject(subject);
        article.setContent(content);
        article.setWriter(writer);
        article.setImages(imgs);
        article.setArtiDate(byline.getDate());
        article.setArtiTime(byline.getTime());

        log.debug(article.toString());

        return article;
    }

}
