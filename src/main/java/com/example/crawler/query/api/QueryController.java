package com.example.crawler.query.api;  // 包声明必须与目录结构匹配

import com.example.crawler.query.QueryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/query")
public class QueryController {
    private final QueryService queryService;

    public QueryController() throws Exception {  // 处理QueryService的IOException
        this.queryService = new QueryService();
    }
}