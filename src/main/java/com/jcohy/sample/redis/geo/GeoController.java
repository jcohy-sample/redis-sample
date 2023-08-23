package com.jcohy.sample.redis.geo;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/8/23:15:42
 * @since 2023.0.1
 */
@RestController
public class GeoController {

    private final GeoService service;

    public GeoController(GeoService service) {
        this.service = service;
    }

    @GetMapping("/geoadd")
    public String geoAdd() {
        return service.geoAdd();
    }

    @GetMapping("/geopos")
    public Point geoPos(String member) {
        return service.geoPos(member);
    }

    @GetMapping("/geohash")
    public String geoHash(String member) {
        return service.geoHash(member);
    }

    @GetMapping("/geodist")
    public Distance geoDist(String member1,String member2) {
        return service.geoDist(member1, member2);
    }

    @GetMapping("/georadius")
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadius() {
        return service.geoRadius();
    }

    @GetMapping("/georadiusbymember")
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadiusByMember() {
        return service.geoRadiusByMember();
    }
}
