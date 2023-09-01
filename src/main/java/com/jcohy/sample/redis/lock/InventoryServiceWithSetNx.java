package com.jcohy.sample.redis.lock;

import cn.hutool.core.lang.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description: V2.0 使用 setnx
 *
 * @author jiac
 * @version 2023.0.1 2023/8/28:16:38
 * @since 2023.0.1
 */
public class InventoryServiceWithSetNx {

    @Value("${server.port}")
    private String port;
    private final StringRedisTemplate template;

    public InventoryServiceWithSetNx(StringRedisTemplate template) {
        this.template = template;
    }

    public String sale() throws InterruptedException {
        String message = "";
        String key = "redisLock";
        String uuid = UUID.randomUUID().toString(true) + ":" + Thread.currentThread().getId();
        Boolean flag = template.opsForValue().setIfAbsent(key, uuid);
        // flag = false ，抢不到的线程要进行重试
        if(!flag) {
            // 5 秒后进行递归重试
            Thread.sleep(5000);
            sale();
        } else {
            // 抢成功
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
                // 释放锁
                template.delete(key);
            }
        }
        return message + "，服务端口号：" + port;
    }
}
