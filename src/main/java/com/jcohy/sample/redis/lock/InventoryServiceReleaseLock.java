package com.jcohy.sample.redis.lock;

import cn.hutool.core.lang.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description: v5.0 实际业务处理时间如果超过了默认设置的 key 的过期时间，就会出现误删锁的情况。
 *
 * @author jiac
 * @version 2023.0.1 2023/8/28:16:38
 * @since 2023.0.1
 */
public class InventoryServiceReleaseLock {

    @Value("${server.port}")
    private String port;
    private final StringRedisTemplate template;

    public InventoryServiceReleaseLock(StringRedisTemplate template) {
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
            // 释放锁，判断加锁与解锁是不是同一个客户端，同一个才行，只能删除自己的锁
            if(template.opsForValue().get(key).equalsIgnoreCase(uuid)) {
                template.delete(key);
            }
        }
        return message + "，服务端口号：" + port;
    }
}
