package com.example.crawl.vo;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;

import java.net.URL;

@Setter
@Getter
public class ArticleResponse {
    private URL url;
    private Document document;
}
