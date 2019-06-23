package com.example.crawl.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
public class PageGen
{

    public PageGen()
    {
        log.debug("<<<<<<<<<<<<<<<<< Create PageGen >>>>>>>>>>>>>>>>>>>>>");
    }


    private Integer cnt = 1;


    public Integer getPageNumber()
    {
        if(cnt <= Config.RECENT_MAX_PAGE) {
            return cnt ++;
        }
        else {
            return null;
        }
    }


}
