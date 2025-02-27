// 文件路径：src/main/java/com/example/crawler/model/ParsedResult.java
package com.example.crawler.model;

import java.util.List;

public class ParsedResult {
    private final String title;
    private final String content;
    private final List<String> links;

    public ParsedResult(String title, String content, List<String> links) {
        this.title = title;
        this.content = content;
        this.links = links;
    }

    // Getter方法
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getLinks() { return links; }
}