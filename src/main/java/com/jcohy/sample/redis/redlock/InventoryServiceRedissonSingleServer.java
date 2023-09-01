package com.jcohy.sample.redis.redlock;

import cn.hutool.core.lang.UUID;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description: V9.0, 使用 redisson
 *
 * @author jiac
 * @version 2023.0.1 2023/8/28:16:38
 * @since 2023.0.1
 */
public class InventoryServiceRedissonSingleServer {

    @Value("${server.port}")
    private String port;
    private final StringRedisTemplate template;

    private final RedissonClient redissonClient;

    public InventoryServiceRedissonSingleServer(StringRedisTemplate template, RedissonClient redissonClient) {
        this.template = template;
        this.redissonClient = redissonClient;
    }

    public String sale() {
        RLock redissonClientLock = this.redissonClient.getLock("redisLock");
        redissonClientLock.lock();
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
            if(redissonClientLock.isLocked() && redissonClientLock.isHeldByCurrentThread()) {
                redissonClientLock.unlock();
            }
        }
        return message + "，服务端口号：" + port;
    }
}
