package com.example.crawl.parser;


import com.example.crawl.parser.chosun.ChosunParser;
import com.example.crawl.parser.khan.KhanBizParser;
import com.example.crawl.parser.khan.KhanNewsParser;
//import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;


@Slf4j
//@RequiredArgsConstructor
@Component
public class ParserSelector
{

//    private final KhanNewsParser newsParser;
//
//    private final KhanBizParser bizParser;
//
//    private final ChosunParser chosunParser;


    private enum ParserDomain
    {
        KHAN_NEWS("news.khan") {
            @Override
            public ArticleParser getArticleParser() {
                return new KhanNewsParser();
            }
        }

        , KHAN_BIZ("biz.khan") {
            @Override
            public ArticleParser getArticleParser() {
                return new KhanBizParser();
            }
        }

        , CHOSUN("chosun.com") {
            @Override
            public ArticleParser getArticleParser() {
                return new ChosunParser();
            }
        }

        , Other("##other##") {
            @Override
            public ArticleParser getArticleParser() {
                log.warn("Unknow parser for domain");
                return null;
            }
        }
        ;


        private String domain;

        ParserDomain(String domain)
        {
            this.domain = domain;
        }

        public abstract ArticleParser getArticleParser();


        public static ArticleParser getParser(String url)
        {
            Optional<ArticleParser> parserDomain =
                    Arrays.stream(ParserDomain.values())
                            .filter(e -> url.contains(e.domain))
                            .map(e -> e.getArticleParser())
                            .findFirst();
            return parserDomain.orElse(Other.getArticleParser());
        }
    }


    public ArticleParser select(String url)
    {
        return ParserDomain.getParser(url);
    }


}
