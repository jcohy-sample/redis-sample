package com.jcohy.sample.redis.redisbigkey;

import java.util.List;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.Tuple;

/**
 * 描述: .
 * <p>
 * Copyright © 2023 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2023/3/11 14:41
 * @since 1.0.0
 */
public class DeleteBigKey {

    // tag::delBigKeyHash[]
    public void delBigKeyHash(String host,int port,String password,String bigHashKey) {
        Jedis jedis = new Jedis(host, port);
        if(password != null && !"".equals(password)) {
            jedis.auth(password);
        }

        ScanParams scanParams = new ScanParams().count(100);
        String cursor = "0";

        do {
            ScanResult<Entry<String, String>> scanResult = jedis.hscan(bigHashKey, cursor, scanParams);
            List<Entry<String, String>> entryList = scanResult.getResult();
            if(entryList !=null && !entryList.isEmpty()) {
                for(Entry<String,String> entry : entryList) {
                    jedis.hdel(bigHashKey,entry.getKey());
                }
            }
            cursor = scanResult.getCursor();
        } while (!"0".equals(cursor));

        jedis.del(bigHashKey);
    }

    // end::delBigKeyHash[]

    // tag::delBigKeyList[]
    public void delBigKeyList(String host,int port,String password,String bigHashKey) {
        Jedis jedis = new Jedis(host, port);
        if(password != null && !"".equals(password)) {
            jedis.auth(password);
        }

        long llen = jedis.llen(bigHashKey);
        int counter = 0;
        int left = 100;

        while(counter < llen) {
            jedis.ltrim(bigHashKey,left,llen);
            counter += left;
        }
        jedis.del(bigHashKey);
    }
    // end::delBigKeyList[]

    // tag::delBigKeySet[]
    public void delBigKeySet(String host,int port,String password,String bigHashKey) {
        Jedis jedis = new Jedis(host, port);
        if(password != null && !"".equals(password)) {
            jedis.auth(password);
        }

        ScanParams scanParams = new ScanParams().count(100);
        String cursor = "0";

        do {
            ScanResult<String> scanResult = jedis.sscan(bigHashKey, cursor, scanParams);
            List<String> memberList = scanResult.getResult();
            if(memberList !=null && !memberList.isEmpty()) {
                for(String member : memberList) {
                    jedis.srem(bigHashKey,member);
                }
            }
            cursor = scanResult.getCursor();
        } while (!"0".equals(cursor));

        jedis.del(bigHashKey);
    }
    // end::delBigKeySet[]

    // tag::delBigKeyZSet[]
    public void delBigKeyZSet(String host,int port,String password,String bigHashKey) {
        Jedis jedis = new Jedis(host, port);
        if(password != null && !"".equals(password)) {
            jedis.auth(password);
        }

        ScanParams scanParams = new ScanParams().count(100);
        String cursor = "0";

        do {
            ScanResult<Tuple> scanResult = jedis.zscan(bigHashKey, cursor, scanParams);
            List<Tuple> tupleList = scanResult.getResult();
            if(tupleList !=null && !tupleList.isEmpty()) {
                for(Tuple tuple : tupleList) {
                    jedis.zrem(bigHashKey,tuple.getElement());
                }
            }
            cursor = scanResult.getCursor();
        } while (!"0".equals(cursor));

        jedis.del(bigHashKey);
    }
    // end::delBigKeyZSet[]


}
