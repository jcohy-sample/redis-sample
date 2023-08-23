package com.jcohy.sample.redis.hyperloglog;

import ch.qos.logback.classic.spi.EventArgUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/8/23:15:18
 * @since 2023.0.1
 */
@Service
public class HyperLogLogService {

    private final RedisTemplate<String,String> template;

    public HyperLogLogService(RedisTemplate<String, String> template) {
        this.template = template;
    }

    @PostConstruct
    public void initIp() {
        new Thread(() -> {
            for (int i = 0; i < 200; i++) {
                Random random = new Random();
                String ip = random.nextInt(256) + "." +
                        random.nextInt(256) + "." +
                        random.nextInt(256) + "." +
                        random.nextInt(256);

                Long hll = template.opsForHyperLogLog().add("hll", ip);
                System.out.println("ip = " + ip + "," + "该 ip 访问首页的次数= " + hll);

                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"t1").start();
    }

    public long uv() {
        return template.opsForHyperLogLog().size("hll");
    }
}
