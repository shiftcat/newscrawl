package com.example.crawl.parser.khan;

import com.example.crawl.entities.ArticleId;
import com.example.crawl.entities.RecentEntity;
import com.example.crawl.entities.Press;
import com.example.crawl.parser.RecentParser;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component
public class KhanRecentParser implements RecentParser
{


    private URIBuilder makeURIBuilder(String link)
    {
        try {
            return new URIBuilder(link);
        } catch (URISyntaxException e) {
            log.error(Throwables.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
    }


    public RecentEntity parse(Element node)
    {
        String subject = node.select("strong.hd_title").text();
        String link = node.select("a").attr("href");

        String artid = null;
        String cate = null;
        List<NameValuePair> queryParams = null;

        URIBuilder uriBuilder = makeURIBuilder(link);
        uriBuilder.setScheme("http");

        link = uriBuilder.toString();

        queryParams = uriBuilder.getQueryParams();

        Optional<NameValuePair> artidFindParam =
                queryParams.stream().filter(qp -> "artid".equals(qp.getName())).findFirst();
        if(artidFindParam.isPresent()) {
            artid = artidFindParam.get().getValue();
        }

        Optional<NameValuePair> cateFindParam =
                queryParams.stream().filter(qp -> "code".equals(qp.getName())).findFirst();
        if(cateFindParam.isPresent()) {
            cate = cateFindParam.get().getValue();
        }
        else {
            cate = "-1";
        }

        Elements ems = node.select("span.byline > em");

        int emsSize = ems.size();

        String writer = null;
        if(emsSize > 2) {
            writer = ems.get(1).text();
        }

        String date = null;
        int dateIndex = -1;
        if(emsSize < 3) dateIndex = 1; else if(emsSize < 4) dateIndex = 2;
        if(dateIndex > 0) {
            date = ems.get(dateIndex).text().replaceAll("\\.\\s*", ".");
        }

        ArticleId articleId = new ArticleId();
        articleId.setId(artid);
        articleId.setPress(Press.KHA);

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


}
