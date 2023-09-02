package com.jcohy.sample.redis.redlock;

import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description: V9.0, 使用 redisson
 *
 * @author jiac
 * @version 2023.0.1 2023/8/28:16:38
 * @since 2023.0.1
 */
public class InventoryServiceRedissonMultiMaster {

    @Value("${server.port}")
    private String port;
    private final StringRedisTemplate template;

    private final RedissonClient redissonClient1;

    private final RedissonClient redissonClient2;
    private final RedissonClient redissonClient3;

    public InventoryServiceRedissonMultiMaster(StringRedisTemplate template, RedissonClient redissonClient1,
                                               RedissonClient redissonClient2,
                                               RedissonClient redissonClient3) {
        this.template = template;
        this.redissonClient1 = redissonClient1;
        this.redissonClient2 = redissonClient2;
        this.redissonClient3 = redissonClient3;
    }

    public String sale() {
        RLock lock1 = this.redissonClient1.getLock("redisLock");
        RLock lock2 = this.redissonClient2.getLock("redisLock");
        RLock lock3 = this.redissonClient3.getLock("redisLock");

        RedissonMultiLock redissonMultiLock = new RedissonMultiLock(lock1, lock2, lock3);
        redissonMultiLock.lock();
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
            redissonMultiLock.unlock();
        }
        return message + "，服务端口号：" + port;
    }
}
