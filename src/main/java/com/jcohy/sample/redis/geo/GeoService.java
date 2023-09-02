package com.jcohy.sample.redis.geo;

import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/8/23:15:42
 * @since 2023.0.1
 */
@Service
public class GeoService {
    private final RedisTemplate<String,String> template;

    public GeoService(RedisTemplate<String,String> template) {
        this.template = template;
    }


    public String geoAdd() {
        Map<String,Point> map = new HashMap<>();
        map.put("天安门",new Point(116.403963,39.915119));
        map.put("故宫",new Point(116.403414,39.924091));
        map.put("长城",new Point(116.024067,40.362619));
        template.opsForGeo().add("city",map);
        return map.toString();
    }

    public Point geoPos(String member) {
        return template.opsForGeo().position("city",member).get(0);
    }

    public String geoHash(String member) {
        return template.opsForGeo().hash("city",member).get(0);
    }

    public Distance geoDist(String member1,String member2) {
        return template.opsForGeo().distance("city",member1,member2, RedisGeoCommands.DistanceUnit.KILOMETERS);
    }

    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadius() {
        // 王府井
        Circle circle = new Circle(116.418017,39.914402, Metrics.KILOMETERS.getMultiplier());
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortDescending()
                .limit(50);
        return template.opsForGeo().radius("city",circle,args);
    }

    @GetMapping("/georadiusbymember")
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadiusByMember() {
        return template.opsForGeo().radius("cite","天安门",Metrics.KILOMETERS.getMultiplier());
    }
}
