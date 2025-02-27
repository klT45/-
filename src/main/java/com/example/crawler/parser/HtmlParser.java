// 文件路径：src/main/java/com/example/crawler/parser/HtmlParser.java
package com.example.crawler.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
import java.util.List;

public class HtmlParser {  // 确保类名与文件名一致

    public static class ParsedResult {  // 内部类无需文件名匹配
        private final String title;
        private final String content;
        private final List<String> links;

        public ParsedResult(String title, String content, List<String> links) {
            this.title = title;
            this.content = content;
            this.links = links;
        }
    }

    public ParsedResult parse(byte[] htmlBytes, String baseUri) {
        Document doc = Jsoup.parse(new String(htmlBytes), baseUri);

        String title = doc.selectFirst("title").text();
        String content = doc.selectFirst("body").text();

        List<String> links = new ArrayList<>();
        for (Element link : doc.select("a[href]")) {
            links.add(link.absUrl("href"));
        }

        return new ParsedResult(title, content, links);
    }
}