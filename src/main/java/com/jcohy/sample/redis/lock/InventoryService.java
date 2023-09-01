package com.jcohy.sample.redis.lock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description: v1.0 初始版本
 *
 * @author jiac
 * @version 2023.0.1 2023/8/28:16:38
 * @since 2023.0.1
 */
public class InventoryService {

    @Value("${server.port}")
    private String port;
    private final StringRedisTemplate template;

    private Lock lock = new ReentrantLock();

    public InventoryService(StringRedisTemplate template) {
        this.template = template;
    }

    public String sale() {
        lock.lock();
        String message = "";
        try {
            // 查询库存信息
            String result = template.opsForValue().get("redisLock");
            int inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            // 判断库存是否充足
            if(inventoryNumber > 0) {
                // 减库存
                template.opsForValue().set("redisLock",String.valueOf(--inventoryNumber));
                message = "成功卖出一个商品，库存剩余" + inventoryNumber;
            } else {
                message = "商品卖完了";
            }
        }finally {
            lock.unlock();
        }
        return message + "，服务端口号：" + port;
    }
}
