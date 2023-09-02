package com.jcohy.sample.redis.bloomfilter;

import java.util.BitSet;
import java.util.Random;
/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/8/23:17:31
 * @since 2023.0.1
 */
public class BloomFilterWithCuckooHashing {

    private BitSet bitSet; // 位数组
    private int[] fingerprints; // 指纹数组
    private int size; // 集合大小
    private int capacity; // 过滤器容量
    private int fingerprintBits; // 指纹位数
    private int numHashFunctions; // 哈希函数数量
    private Random random; // 随机数生成器

    public BloomFilterWithCuckooHashing(int size, int capacity, int fingerprintBits, int numHashFunctions) {
        this.size = size;
        this.capacity = capacity;
        this.fingerprintBits = fingerprintBits;
        this.numHashFunctions = numHashFunctions;
        this.bitSet = new BitSet(size);
        this.fingerprints = new int[size];
        this.random = new Random();
    }

    // 插入元素
    public void insert(String element) {
        int fingerprint = getFingerprint(element);
        int hash1 = hashFunction1(element);
        int hash2 = hashFunction2(element);

        if (bitSet.get(hash1) && fingerprints[hash1] == fingerprint) {
            return; // 元素可能已存在，无需重复插入
        }

        if (bitSet.get(hash2) && fingerprints[hash2] == fingerprint) {
            return; // 元素可能已存在，无需重复插入
        }

        if (bitSet.get(hash1) && bitSet.get(hash2)) {
            // 发生冲突，开始进行布谷鸟操作
            int index = random.nextBoolean() ? hash1 : hash2;
            int evictFingerprint = fingerprints[index];

            // 随机选择一个替换位置
            int randomIndex = random.nextBoolean() ? hashFunction1(String.valueOf(evictFingerprint)) : hashFunction2(String.valueOf(evictFingerprint));
            int temp = fingerprints[randomIndex];
            bitSet.set(randomIndex);
            fingerprints[randomIndex] = evictFingerprint;

            // 循环替换直到没有冲突
            while (bitSet.get(randomIndex)) {
                randomIndex = random.nextBoolean() ? hashFunction1(String.valueOf(temp)) : hashFunction2(String.valueOf(temp));
                temp = fingerprints[randomIndex];
                bitSet.set(randomIndex);
                fingerprints[randomIndex] = evictFingerprint;
            }

            // 将新元素插入
            int insertIndex = random.nextBoolean() ? hash1 : hash2;
            bitSet.set(insertIndex);
            fingerprints[insertIndex] = fingerprint;
        } else {
            // 两个位置至少有一个为空，直接插入
            bitSet.set(hash1);
            fingerprints[hash1] = fingerprint;
            bitSet.set(hash2);
            fingerprints[hash2] = fingerprint;
        }
    }

    // 判断元素是否存在
    public boolean contains(String element) {
        int fingerprint = getFingerprint(element);
        int hash1 = hashFunction1(element);
        int hash2 = hashFunction2(element);

        return (bitSet.get(hash1) && fingerprints[hash1] == fingerprint)
                || (bitSet.get(hash2) && fingerprints[hash2] == fingerprint);
    }

    // 获取指纹
    private int getFingerprint(String element) {
        // 使用简单的位运算获取指纹
        int hash = element.hashCode();
        return hash % (1 << fingerprintBits);
    }

    // 哈希函数1
    private int hashFunction1(String element) {
        int hash = element.hashCode();
        return Math.abs(hash % size);
    }

    // 哈希函数2
    private int hashFunction2(String element) {
        int hash = element.hashCode();
        return Math.abs(((hash >> 16) ^ hash) % size);
    }

    public static void main(String[] args) {
        BloomFilterWithCuckooHashing filter = new BloomFilterWithCuckooHashing(16, 8, 4, 2);

        filter.insert("apple");
        filter.insert("banana");
        filter.insert("orange");

        System.out.println(filter.contains("apple")); // 输出 true
        System.out.println(filter.contains("pear")); // 输出 false

        filter.insert("apple"); // 重复插入元素

        System.out.println(filter.contains("apple")); // 输出 true

        filter.insert("grape"); // 发生冲突

        System.out.println(filter.contains("grape")); // 输出 true

        filter.insert("mango"); // 再次插入元素，发生布谷鸟操作

        System.out.println(filter.contains("mango")); // 输出 true

        System.out.println(filter.contains("cherry")); // 输出 false
    }
}
