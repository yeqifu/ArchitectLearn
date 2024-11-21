package com.yeqifu;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * @author: yeqifu
 * @date: 2024/8/8 10:15
 */
public class Acceptor extends ServerThread {
    private final ServerSocketChannel serverSocketChannel;

    private final Server server;

    private final Collection<PollerIo> ioThreads;

    private Iterator<PollerIo> ioIterator;

    public Acceptor(String name, Server server, Set<PollerIo> ioThreads) throws IOException {
        super(name);
        this.server = server;
        // acceptor 也就是 mainReactor 初始化的时候就创建一个 serverSocketChannel，然后监听 accept 事件
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        this.ioThreads = Collections.unmodifiableList(new ArrayList<>(ioThreads));
        // 为什么要构造出一个迭代器，因为通过一个迭代器可以拿到一个 pollerIO
        this.ioIterator = this.ioThreads.iterator();
    }

    @Override
    public void run() {
        // 服务端没有关闭，且 serverSocketChannel 上的 socket 没有被关闭，那么就一直轮询
        while (!server.stopped && !serverSocketChannel.socket().isClosed()) {
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
                if (selectionKey.isAcceptable()) {
                    try {
                        doAccept(selectionKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // 如果服务端关闭了或者 serverSocketChannel 的 socket 关闭了，就关闭 IO 多路复用器
        closeSelector();
    }

    /**
     * 接收一个新的连接，并将它给到 subReactor 线程
     *
     * @param selectionKey
     * @throws IOException
     */
    private void doAccept(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = this.serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        // get pollerIo，than register to selector
        if (!ioIterator.hasNext()) {
            this.ioIterator = this.ioThreads.iterator();
        }
        PollerIo pollerIo = ioIterator.next();
        pollerIo.addAcceptedConnection(socketChannel);
    }
}
