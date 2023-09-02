package com.jcohy.sample.redis.bloomfilter;

import java.util.Arrays;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/8/23:17:32
 * @since 2023.0.1
 */
public class CuckooFilter {

    private int capacity; // 过滤器容量
    private int[] table; // 位数组
    private int fingerprintBits; // 指纹位数

    public CuckooFilter(int capacity, int fingerprintBits) {
        this.capacity = capacity;
        this.table = new int[capacity];
        this.fingerprintBits = fingerprintBits;
    }

    // 插入元素
    public void insert(int element) {
        int fingerprint = getFingerprint(element);

        // 查找空闲的位置插入
        for (int i = 0; i < capacity; i++) {
            if (table[i] == 0) {
                table[i] = fingerprint;
                return;
            }
        }

        // 如果没有空闲位置，使用随机位置替换
        int pos = (int) (Math.random() * capacity);
        int temp = table[pos];
        table[pos] = fingerprint;

        // 递归插入被替换出的指纹
        insert(temp);
    }

    // 判断元素是否存在
    public boolean contains(int element) {
        int fingerprint = getFingerprint(element);

        for (int i = 0; i < capacity; i++) {
            if (table[i] == fingerprint) {
                return true;
            }
        }

        return false;
    }

    // 删除元素
    public void remove(int element) {
        int fingerprint = getFingerprint(element);

        // 查找并删除指纹
        for (int i = 0; i < capacity; i++) {
            if (table[i] == fingerprint) {
                table[i] = 0;
                return;
            }
        }
    }

    // 获取指纹
    private int getFingerprint(int element) {
        // 使用简单的位运算获取指纹
        return element % (1 << fingerprintBits);
    }

    @Override
    public String toString() {
        return Arrays.toString(table);
    }

    public static void main(String[] args) {
        CuckooFilter filter = new CuckooFilter(8, 4);

        filter.insert(10);
        filter.insert(20);
        filter.insert(30);

        System.out.println(filter.contains(20)); // 输出 true
        System.out.println(filter.contains(40)); // 输出 false

        filter.remove(20);

        System.out.println(filter.contains(20)); // 输出 false

        System.out.println(filter);
    }
}
