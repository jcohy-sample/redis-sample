package com.jcohy.sample.redis.lock;

import cn.hutool.core.lang.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:v 4.0 当部署的微服务的 Java 程序挂了，代码层面根本没有走到 finally 块中，没办法保证解锁，无过期时间，该 key 一直存在，这个 key 没有被删除，需要加上一个过期时间
 *
 * @author jiac
 * @version 2023.0.1 2023/8/28:16:38
 * @since 2023.0.1
 */
public class InventoryServiceWithExpireTime {

    @Value("${server.port}")
    private String port;
    private final StringRedisTemplate template;

    public InventoryServiceWithExpireTime(StringRedisTemplate template) {
        this.template = template;
    }

    public String sale() throws InterruptedException {
        String message = "";
        String key = "redisLock";
        String uuid = UUID.randomUUID().toString(true) + ":" + Thread.currentThread().getId();

        // 使用自旋替代递归方法重试调用
        // 加入 30s 过期时间,注意，加锁和过期时间必须在同一行
        while (Boolean.FALSE.equals(template.opsForValue().setIfAbsent(key, uuid,Duration.ofSeconds(30)))) {
            // 5 秒后进行递归重试
            Thread.sleep(5000);
        }

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
        return message + "，服务端口号：" + port;
    }
}
