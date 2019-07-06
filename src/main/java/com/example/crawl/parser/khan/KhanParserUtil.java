package com.example.crawl.parser.khan;

import com.example.crawl.vo.Byline;
import com.example.crawl.vo.ImgTag;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

public class KhanParserUtil
{


    public static Byline parserByline(Document node)
    {
        Byline byline = new Byline();

        Elements emByline = node.select("div.byline > em");

        if(!emByline.isEmpty()) {
            String bylineText = null;

            if(emByline.size() > 1) {
                bylineText = emByline.get(1).text();
                byline.setUpdated(true);
            }
            else {
                bylineText = emByline.get(0).text();
            }

            String[] spitedByline = bylineText.split(" ");

            // 입력 : 2019.07.05 10:56
            if(spitedByline.length > 2) {
                byline.setDate(spitedByline[2]);
            }
            if(spitedByline.length > 3) {
                byline.setTime(spitedByline[3]);
            }
        }

        return byline;
    }




    public static List<ImgTag> parserImgTag(Document node)
    {
        // div[contains(@class, 'art_photo photo_center
        Elements imgNodes = node.select("div.art_photo > div.art_photo_wrap");

        return
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
    }


}
