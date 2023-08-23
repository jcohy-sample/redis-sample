package com.jcohy.sample.redis.bloomfilter;

import java.util.BitSet;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/8/23:17:33
 * @since 2023.0.1
 */
public class BloomFilter {

    private BitSet bitSet; // 位数组
    private int size; // 集合大小
    private int numHashFunctions; // 哈希函数数量

    public BloomFilter(int size, int numHashFunctions) {
        this.size = size;
        this.numHashFunctions = numHashFunctions;
        this.bitSet = new BitSet(size);
    }

    // 插入元素
    public void insert(String element) {
        for (int i = 0; i < numHashFunctions; i++) {
            int hash = hashFunction(element, i);
            bitSet.set(hash);
        }
    }

    // 判断元素是否存在
    public boolean contains(String element) {
        for (int i = 0; i < numHashFunctions; i++) {
            int hash = hashFunction(element, i);
            if (!bitSet.get(hash)) {
                return false; // 只要有一个哈希位为0，则元素一定不存在
            }
        }
        return true; // 所有哈希位都为1，则认为元素可能存在
    }

    // 哈希函数
    private int hashFunction(String element, int seed) {
        int hash = 0;
        for (int i = 0; i < element.length(); i++) {
            hash = seed * hash + element.charAt(i);
        }
        return Math.abs(hash) % size;
    }

    public static void main(String[] args) {
        BloomFilter filter = new BloomFilter(32, 3);

        filter.insert("apple");
        filter.insert("banana");
        filter.insert("orange");

        System.out.println(filter.contains("apple")); // 输出 true
        System.out.println(filter.contains("pear")); // 输出 false

        filter.insert("apple"); // 重复插入元素

        System.out.println(filter.contains("apple")); // 输出 true

        filter.insert("grape");

        System.out.println(filter.contains("grape")); // 输出 true

        System.out.println(filter.contains("cherry")); // 输出 false
    }
}