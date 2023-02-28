package com.jcohy.sample.cluster;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class RedisTemplateClusterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisTemplateClusterApplication.class,args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(RedisTemplate<String,String> template) {
        return args -> template.opsForValue().set("k1","v1");
    }
}
