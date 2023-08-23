package com.jcohy.sample.redis.bitmap;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/8/23:16:35
 * @since 2023.0.1
 */
public class Hash {

    public static void main(String[] args) {
        // 哈希冲突示例
        System.out.println("Aa".hashCode());
        System.out.println("BB".hashCode());

        System.out.println("柳柴".hashCode());
        System.out.println("柴柕".hashCode());

        Set<Integer> sets = new HashSet<>();
        int hashCode;
        for (int i = 0; i < 200000; i++) {
            hashCode = new Object().hashCode();
            if(sets.contains(hashCode)) {
                System.out.println("运行到第 " + i + "次，出现了哈希冲突，hashCode = " + hashCode);
                continue;
            }
            sets.add(hashCode);
        }
    }
}
