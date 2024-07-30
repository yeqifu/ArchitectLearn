package com.yeqifu;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @author: yeqifu
 * @date: 2024/7/21 21:49
 */
public class NioServer {
    public static void main(String[] args) throws IOException {
        // 创建一个 serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 绑定到 9999 端口
        serverSocketChannel.bind(new InetSocketAddress(9999));
        // 创建一个 selector
        Selector selector = Selector.open();
        // 将 serverSocketChannel 注册到 selector 上，第二个参数代表监听 OP_ACCEPT 事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // selector 执行 select，selectionKey 会得到 socket 相应的事件
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                // 拿到 selectionKey 之后将当前事件从事件集合 Set 中删除
                iterator.remove();
                processSelectedKey(selector, selectionKey);
            }
        }
    }

    private static void processSelectedKey(Selector selector, SelectionKey selectionKey) throws IOException {
        if (selectionKey.isValid()) {
            if (selectionKey.isAcceptable()) {
                // 处理 accept 事件
                dearAccept(selector, selectionKey);
            } else if (selectionKey.isReadable()) {
                // 处理 read 事件
                dearRead(selector, selectionKey);
            }
        }
    }

    private static void dearRead(Selector selector, SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        // 将客户端发来的数据读到缓冲区中去
        int read = socketChannel.read(readBuffer);
        if (read > 0) {
            // 切换读写模式
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.remaining()];
            // 将缓冲区的数据给到 bytes
            readBuffer.get(bytes);
            String clientMessage = new String(bytes, Charset.defaultCharset());
            System.out.println("服务端接收到客户端的数据：" + clientMessage);
            readBuffer.clear();
            // 进行业务操作

            // 给客户端发送数据
            byte[] sendMessage = "hello client, i am server".getBytes(StandardCharsets.UTF_8);
            ByteBuffer writeBuffer = ByteBuffer.allocate(sendMessage.length);
            writeBuffer.put(sendMessage);
            // 切换成写模式
            writeBuffer.flip();
            socketChannel.write(writeBuffer);
            writeBuffer.clear();
        }
    }

    private static void dearAccept(Selector selector, SelectionKey selectionKey) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }
}
