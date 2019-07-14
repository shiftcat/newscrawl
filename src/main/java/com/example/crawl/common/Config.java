package com.example.crawl.common;

public class Config
{

    // "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36",
    // "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
    // "Accept-Encoding": "gzip, deflate"

    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

    /**
     * 최신 기사 최대 페이지 수
     *
     * 여기에 정의된 수 만큼 Target 서버에 요청하여 최신 기사 목록을 스크랩 한다.
     */
    public static final int RECENT_MAX_PAGE = 3;


    /**
     * 기사 본문 최대 스크랩 수
     *
     * 여기에 정의된 수 만큼 Target 서버에 요청하여 기사 본문을 스크랩 한다.
     *
     */
    public static final int ARTICLE_SCRAP_SIZE = 50;


    public static final int CONNECTION_TIME_OUT = 1000 * 30;
}
