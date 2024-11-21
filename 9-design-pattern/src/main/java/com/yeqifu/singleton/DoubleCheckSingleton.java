package com.yeqifu.singleton;

/**
 * @author: yeqifu
 * @date: 2024/8/20 9:16
 */
public class DoubleCheckSingleton {

    private static DoubleCheckSingleton doubleCheckSingleton = null;

    /**
     * 私有化构造器
     */
    private DoubleCheckSingleton() {

    }

    /**
     * 提供获得类对象的静态方法
     *
     * @return DoubleCheckSingleton 类对象
     */
    public static DoubleCheckSingleton getDoubleCheckSingleton() {
        if (null == doubleCheckSingleton) {
            synchronized (DoubleCheckSingleton.class) {
                if (null == doubleCheckSingleton) {
                    doubleCheckSingleton = new DoubleCheckSingleton();
                }
            }
        }
        return doubleCheckSingleton;
    }
}
