package com.jcohy.sample.redis.hyperloglog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/8/23:15:18
 * @since 2023.0.1
 */
@Controller
public class HyperLogLogController {

    private final HyperLogLogService service;

    public HyperLogLogController(HyperLogLogService service) {
        this.service = service;
    }

    @GetMapping("/ip")
    public long uv() {
        return service.uv() ;
    }
}
