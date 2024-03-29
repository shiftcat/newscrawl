package com.example.crawl.parser.chosun;

import com.example.crawl.entities.ArticleEntity;
import com.example.crawl.vo.ArticleId;
import com.example.crawl.vo.Byline;
import com.example.crawl.vo.ImgTag;
import com.example.crawl.entities.RecentEntity;
import com.example.crawl.vo.Press;
import com.example.crawl.parser.ArticleParser;
import com.example.crawl.parser.RecentParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 *
 * 조선일보 뉴스 파서 클래스
 *
 */

@Slf4j
@Component
public class ChosunParser implements RecentParser, ArticleParser
{

    private String getArticleId(String link)
    {
        String[] sl = link.split("/");
        String fn = sl[sl.length-1];
        return fn.replaceAll(".html", "");
    }


    public RecentEntity parse(Element node)
    {
        String artid = null;
        String subject = null;
        String writer = null;
        String link = null;
        String cate = null;
        String date = null;

        link = node.select("dt > a").attr("href");
        subject = node.select("dt > a").text();
        artid = getArticleId(link);

        date = node.select("dd.date_author > span.date").text();
        writer = node.select("dd.date_author > span.author").text();

        ArticleId articleId = new ArticleId();
        articleId.setId(artid);
        articleId.setPress(Press.CHO);

        RecentEntity entity = new RecentEntity();
        entity.setArticleId(articleId);
        entity.setSubject(subject);
        entity.setWriter(writer);
        entity.setLink(link);
        entity.setCate(cate);
        entity.setDate(date);

        log.debug(entity.toString());

        return entity;
    }


    private String findCate(Document doc)
    {
        String cate = null;

        Elements elements1 = doc.select("script");

        Optional<String> scriptHasCatID =
                elements1.stream()
                        .map(e -> e.html())
                        .filter(e -> e.contains("CatID"))
                        .findFirst();

        if(scriptHasCatID.isPresent()) {
            Optional<String> codeLines =
                    Arrays.stream(scriptHasCatID.get()
                            .split("\n"))
                            .filter(e-> e.contains("CatID"))
                            .findFirst();
            if(codeLines.isPresent()) {
                String catCode = codeLines.get();
                log.debug("catCode => " + catCode);

                Pattern p = Pattern.compile("(?is)CatId = \"(.+?)\"");
                Matcher m = p.matcher(catCode);
                if(m.find()) {
                    cate = m.group(1);
                    log.debug("CatID => " + cate);
                }
            }
        }

        return cate;
    }




    private Byline getByline(Document doc)
    {
        Byline byline = new Byline();

        String newsDate = doc.select("div.news_body > div.news_date").text();
        log.debug("News date : " + newsDate);
        String[] splitedNewsDate = null;
        if(newsDate.contains("수정")) {
            String[] slicedNewsDate = newsDate.split("\\|");
            log.debug("slicedNewsDate => " + Arrays.toString(slicedNewsDate));
            splitedNewsDate = slicedNewsDate[1].trim().split(" ");
            byline.setUpdated(true);
        }
        else {
            splitedNewsDate = newsDate.split(" ");
        }
        log.debug("splitedNewsDate => " + Arrays.toString(splitedNewsDate));

        if(splitedNewsDate.length > 1) {
            byline.setDate(splitedNewsDate[1]);
        }
        if(splitedNewsDate.length > 2) {
            byline.setTime(splitedNewsDate[2]);
        }

        return byline;
    }



    private List<ImgTag> getImgs(Document doc)
    {
        Elements imgElements = doc.select("div.news_body > div.news_imgbox");

        return
            imgElements.stream()
                .map(element -> {
                    Elements imgElement = element.select("figure > img");

                    String src = null;
                    if(imgElement.isEmpty()) {
                        Elements zoopImgElement = element.select("figure > span.zoom_img");
                        src = zoopImgElement.select("a > img").attr("src");
                    }
                    else {
                        src = imgElement.attr("src");
                    }

                    String alt = element.select("figure > figcaption").text();

                    ImgTag imgTag = new ImgTag();
                    imgTag.setSrc(src);
                    imgTag.setAlt(alt);
                    return imgTag;
                })
                .collect(Collectors.toList());
    }



    public ArticleEntity parse(Document doc)
    {
        String subject = doc.select("div.news_title_text > h1").text();

        Elements bodyElements = doc.select("div.news_body > div.par");
        List<String> contents = bodyElements.stream()
                .map(element -> element.text())
                .collect(Collectors.toList());

        String content = String.join("\n ", contents);

        Byline byline = getByline(doc);
        List<ImgTag> imgs = getImgs(doc);
        String cate = findCate(doc);

        ArticleEntity article = new ArticleEntity();
        article.setSubject(subject);
        article.setContent(content);
        article.setCate(cate);
        article.setImages(imgs);
        article.setArtiDate(byline.getDate());
        article.setArtiTime(byline.getTime());
        article.setUpdated(byline.isUpdated());

        log.debug(article.toString());

        return article;
    }

}
