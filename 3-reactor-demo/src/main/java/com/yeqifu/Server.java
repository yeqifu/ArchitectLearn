package com.yeqifu;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author: yeqifu
 * @date: 2024/8/8 10:17
 */
public class Server {

    private int port;

    private Acceptor acceptor;

    private Set<PollerIo> ioThreads;

    private ExecutorService businessExecutor;

    /**
     * 保证可见性：当一个线程修改了用 volatile 修饰的变量后，修改后的值会立刻被更新到主内存中，
     * 而读取该变量的其他线程会直接从主内存中读取最新的值，而不是从自己的工作内存中缓存的值。
     * 防止指令重排序：volatile 关键字还禁止了变量读写操作的指令重排序。这意味着在访问 volatile 变量之前的操作不会被重排序到 volatile 变量之后，反之亦然。这在一定程度上保证了多线程操作的有序性。
     */
    public volatile boolean stopped = true;

    public int getPort() {
        return port;
    }

    public void init() {
        // 读配置文件
        this.port = 9999;
        // prepare io thread
        // io 线程的最大数量取 4 和 cpu 核数 * 2 两者的最大值
        int ioNumbers = Math.max(4, Runtime.getRuntime().availableProcessors() * 2);
        this.ioThreads = new HashSet<>(ioNumbers);
        for (int i = 0; i < ioNumbers; i++) {
            PollerIo pollerIo = new PollerIo("poller" + i, this);
            this.ioThreads.add(pollerIo);
        }
        // prepare acceptor thread
        try {
            this.acceptor = new Acceptor("acceptor", this, this.ioThreads);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        this.businessExecutor = new ThreadPoolExecutor(
                200,
                500,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10000)
        );
        for (PollerIo ioThread : ioThreads) {
            ioThread.start();
        }
        this.acceptor.start();
    }

    public void shutdown() {
        this.stopped = true;
        this.businessExecutor.shutdown();
    }

    public void addBusinessTask(BusinessTask businessTask) {
        this.businessExecutor.execute(businessTask);
    }

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Server server = new Server();
        server.init();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            server.shutdown();
            countDownLatch.countDown();
        }));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("server 退出...");
    }

}
