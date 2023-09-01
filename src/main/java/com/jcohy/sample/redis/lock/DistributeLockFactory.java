package com.jcohy.sample.redis.lock;

import cn.hutool.core.lang.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/8/29:15:38
 * @since 2023.0.1
 */
@Component
public class DistributeLockFactory {

    private final StringRedisTemplate template;

    private String lockName;

    private String uuid;

    public DistributeLockFactory(StringRedisTemplate template ) {
        this.template = template;
        this.uuid = UUID.randomUUID().toString(true) + ":" + Thread.currentThread().getId();
    }

    public Lock getDistributeLock(String lockType) {
        if(null == lockType) {
            return null;
        }
        if(lockType.equalsIgnoreCase("redis")) {
            lockName = "redisLock";
            return new InventoryServiceWithReentrancy.RedisDistributeLock(template,lockName,uuid);
        }

        if(lockType.equalsIgnoreCase("zookeeper")) {
            lockName = "zookeeperLock";
            //todo zookeeper 分布式锁实现
        }
        if(lockType.equalsIgnoreCase("mysql")) {
            lockName = "zookeeperLock";
            //todo mysql 分布式锁实现
        }
        return null;
    }
}
