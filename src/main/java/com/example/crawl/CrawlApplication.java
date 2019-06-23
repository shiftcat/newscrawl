package com.example.crawl;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class CrawlApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrawlApplication.class, args);
    }
}
