package com.example.crawl.parser;

import com.example.crawl.entities.ArticleEntity;
import org.jsoup.nodes.Document;

public interface ArticleParser
{
    public ArticleEntity parse(Document node);
}
