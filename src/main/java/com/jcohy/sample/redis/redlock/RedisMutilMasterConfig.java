package com.jcohy.sample.redis.redlock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/8/30:10:27
 * @since 2023.0.1
 */
@Configuration
public class RedisMutilMasterConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedissonClient redissonClient1() {
        Config config = new Config();
        config.useSingleServer()
                // use "rediss://" for SSL connection
                .setAddress("redis://127.0.0.1:6379")
                .setTimeout(3000)
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(5)
                .setDatabase(0)
                .setPassword("1111");
        return Redisson.create(config);
    }

    @Bean
    public RedissonClient redissonClient2() {
        Config config = new Config();
        config.useSingleServer()
                // use "rediss://" for SSL connection
                .setAddress("redis://127.0.0.1:6378")
                .setTimeout(3000)
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(5)
                .setDatabase(0)
                .setPassword("1111");
        return Redisson.create(config);
    }

    @Bean
    public RedissonClient redissonClient3() {
        Config config = new Config();
        config.useSingleServer()
                // use "rediss://" for SSL connection
                .setAddress("redis://127.0.0.1:6377")
                .setTimeout(3000)
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(5)
                .setDatabase(0)
                .setPassword("1111");
        return Redisson.create(config);
    }
}
