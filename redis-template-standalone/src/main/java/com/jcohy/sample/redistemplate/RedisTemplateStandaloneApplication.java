package com.jcohy.sample.redistemplate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class RedisTemplateStandaloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisTemplateStandaloneApplication.class,args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(RedisTemplate<String,String> template) {
        return args -> template.opsForValue().set("k1","v1");
    }
}
