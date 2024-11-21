package com.yeqifu;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: yeqifu
 * @date: 2024/8/8 10:17
 */
public class PollerIo extends ServerThread {

    private final Server server;

    private final Queue<SocketChannel> acceptedQueue;

    public PollerIo(String name, Server server) {
        super(name);
        this.server = server;
        this.acceptedQueue = new LinkedBlockingQueue<>();
    }

    /**
     * subReactor 接收新的 socketChannel 连接
     *
     * @param socketChannel
     */
    public void addAcceptedConnection(SocketChannel socketChannel) {
        this.acceptedQueue.offer(socketChannel);
        // 如果 subReactor 上的 socketChannel 一直没有事件就会阻塞，所以需要先唤醒 selector
        wakeupSelector();
    }

    @Override
    public void run() {
        while (!server.stopped) {
            doSelect();
            doAcceptedConnections();
        }
        closeSelector();
    }

    /**
     * 从 queue 中拿到 mainReactor 提交的 socketChannel，并将其注册到 subReactor 中的 selector
     */
    private void doAcceptedConnections() {
        SocketChannel socketChannel;
        while (!server.stopped && (socketChannel = acceptedQueue.poll()) != null) {
            try {
                socketChannel.register(this.selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检测客户端 socketChannel 是否有读写事件
     */
    private void doSelect() {
        try {
            this.selector.select();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            iterator.remove();
            if (!selectionKey.isValid()) {
                continue;
            }
            if (selectionKey.isReadable() || selectionKey.isWritable()) {
                handleIo(selectionKey);
            }
        }
    }

    /**
     * 处理客户端的读写事件，并将读写数据给到业务线程
     *
     * @param selectionKey
     */
    private void handleIo(SelectionKey selectionKey) {
        if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            int i = 0;
            try {
                i = socketChannel.read(readBuffer);
            } catch (IOException e) {
                try {
                    socketChannel.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            if (i > 0) {
                BusinessTask businessTask = new BusinessTask(readBuffer, socketChannel);
                server.addBusinessTask(businessTask);
            }
        }
    }

}
