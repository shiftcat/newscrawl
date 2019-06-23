package com.example.crawl.parser.khan;

import com.example.crawl.entities.ArticleEntity;
import com.example.crawl.entities.ImgTag;
import com.example.crawl.parser.ArticleParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class KhanNewsParser implements ArticleParser
{

    public ArticleEntity parse(Document node)
    {
        // .//div[contains(@class, 'art_header')]/div[@class='subject']/h1
        String subject = node.select("div.art_header > div.subject > h1").text();

        // .//div[@class='art_body']/p[@class='content_text']
        String article = node.select("div.art_body > p.content_text").text();

        // div[contains(@class, 'art_header')]/div[@class='subject']/span/a
        String writer = node.select("div.art_header > div.subject > span > a").text();

        // div[contains(@class, 'art_photo photo_center
        Elements imgNodes = node.select("div.art_photo > div.art_photo_wrap");
        List<ImgTag> imgs =
                imgNodes.stream().map(e -> {
                    Element imgElem = e.select("img").first();
                    String src = imgElem.attr("src");
                    String alt = imgElem.attr("alt");

                    ImgTag imgTag = new ImgTag();
                    imgTag.setSrc(src);
                    imgTag.setAlt(alt);
                    return imgTag;
                })
                .collect(Collectors.toList());

        ArticleEntity entity = new ArticleEntity();
        entity.setSubject(subject);
        entity.setArticle(article);
        entity.setWriter(writer);
        entity.setImages(imgs);

        log.debug(entity.toString());

        return entity;
    }

}
