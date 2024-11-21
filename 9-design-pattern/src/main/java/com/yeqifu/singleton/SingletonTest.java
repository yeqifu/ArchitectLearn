package com.yeqifu.singleton;

/**
 * @author: yeqifu
 * @date: 2024/8/20 9:35
 */
public class SingletonTest {
    public static void main(String[] args) {
        DoubleCheckSingleton doubleCheckSingleton = DoubleCheckSingleton.getDoubleCheckSingleton();
        System.out.println(doubleCheckSingleton);
        DoubleCheckSingleton doubleCheckSingletonTwo = DoubleCheckSingleton.getDoubleCheckSingleton();
        System.out.println(doubleCheckSingletonTwo);
    }
}
