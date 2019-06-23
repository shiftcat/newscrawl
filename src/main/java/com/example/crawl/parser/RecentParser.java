package com.example.crawl.parser;

import com.example.crawl.entities.RecentEntity;
import org.jsoup.nodes.Element;

public interface RecentParser
{
    public RecentEntity parse(Element node);
}
