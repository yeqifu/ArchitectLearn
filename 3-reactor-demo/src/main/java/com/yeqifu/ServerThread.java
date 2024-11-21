package com.yeqifu;

import java.io.IOException;
import java.nio.channels.Selector;

/**
 * 服务端线程基类
 * 该基类主要是进行 selector 相关处理，因为 mainReactor 和 subReactor 都需要 IO 多路复用器-epoll，即 selector
 * mainReactor 的 selector 监听 accept 事件，然后将 socketChannel 给到 subReactor ，subReactor 上的 selector 主要是监听注册到它上面的 socketChannel 的读写事件
 * 所以服务端线程基类封装了对 selector 的一些处理
 *
 * @author: yeqifu
 * @date: 2024/8/8 10:13
 */
public abstract class ServerThread extends Thread {

    protected Selector selector;

    public ServerThread(String name) {
        super(name);
        try {
            // 类被初始化了就打开 IO 多路复用器
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭 IO 多路复用器
     */
    protected void closeSelector() {
        try {
            this.selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 唤醒 IO 多路复用器
     */
    protected void wakeupSelector() {
        // 唤醒，结束 selector 的阻塞
        this.selector.wakeup();
    }
}
