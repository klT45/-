package com.example.crawler.storage;  // 修正包路径

import com.example.crawler.model.ParsedResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStorage {

    public void saveToFile(ParsedResult result, String filename)
            throws StorageException {

        try {
            String content = formatContent(result);
            Files.writeString(Path.of(filename), content);
        } catch (IOException | NullPointerException e) {  // 增加空指针检查
            throw new StorageException("文件保存失败: " + filename, e);
        }
    }

    private String formatContent(ParsedResult result) {
        return String.format("标题: %s\n内容摘要: %.100s...\n链接数: %d",  // 限制内容长度
                safeGet(result.getTitle()),
                safeGet(result.getContent()),
                result.getLinks().size());
    }

    private String safeGet(String value) {
        return value != null ? value : "[无内容]";  // 空值保护
    }

    // 将异常类提升为顶级类
    public static class StorageException extends Exception {
        public StorageException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }
}