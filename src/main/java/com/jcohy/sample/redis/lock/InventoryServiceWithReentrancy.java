package com.jcohy.sample.redis.lock;

import cn.hutool.core.lang.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description: V7.0, 使用 lua 脚本完成 lock/unlock 可重入锁
 *
 * @author jiac
 * @version 2023.0.1 2023/8/28:16:38
 * @since 2023.0.1
 */
public class InventoryServiceWithReentrancy {

    @Value("${server.port}")
    private String port;
    private final StringRedisTemplate template;

    private Lock lock;

    public InventoryServiceWithReentrancy(StringRedisTemplate template,String uuid) {
        this.template = template;
        this.lock = new RedisDistributeLock(template,"redisLock",uuid);
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

    /**
     * 自研分布式锁
     */
    static class RedisDistributeLock implements Lock {

        private final StringRedisTemplate template;

        // KEYS[1]
        private String lockName;

        // ARGV[1]
        private String uuid;

        // ARGV[2]
        private long expireTime;

        public RedisDistributeLock(StringRedisTemplate template,String lockName,String uuid) {
            this.template = template;
            this.lockName = lockName;
            this.uuid = uuid;
            this.expireTime = 50L;
        }

        String lockScript = """
                if redis.call('exists',KEYS[1]) == 0 or redis.call('hexists',KEYS[1],ARGV[1]) == 1 then
                    redis.call('hincrby',KEYS[1],ARGV[1],1)
                    redis.call('expire',KEYS[1],ARGV[2])
                    return 1
                else
                    return 0
                end""";

        String unLockScript = """
                if redis.call('hexists',KEYS[1]) == 0 then
                    return nil
                elseif redis.call('hincrby',KEYS[1],ARGV[1],-1) == 0 then
                    redis.call('delete',KEYS[1])
                else
                    return 0
                end""";
        @Override
        public void lock() {
            tryLock();
        }


        @Override
        public void unlock() {
            Long flag = template.execute(new DefaultRedisScript<>(unLockScript, Long.class), Arrays.asList(lockName), uuid, expireTime);
            if( null == flag) {
                throw new RuntimeException("this lock doesn't exists!");
            }
        }

        @Override
        public boolean tryLock() {
            try {
                return tryLock(-1,TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
            if(time == -1) {
                while (!template.execute(new DefaultRedisScript<>(lockScript,Boolean.class), Arrays.asList(lockName),uuid,expireTime)) {
                    // 暂停 60s
                    TimeUnit.SECONDS.sleep(60);
                }
                return true;
            }
            return false;
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @NotNull
        @Override
        public Condition newCondition() {
            return null;
        }
    }
}
