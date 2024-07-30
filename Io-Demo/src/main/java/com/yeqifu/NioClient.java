package com.yeqifu;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author: yeqifu
 * @date: 2024/7/21 22:25
 */
public class NioClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        boolean connect = socketChannel.connect(new InetSocketAddress("127.0.0.1", 9999));
        System.out.println("客户端是否成功连接到服务端：" + connect);
        byte[] bytes = "hello, i am client".getBytes(StandardCharsets.UTF_8);
        ByteBuffer allocate = ByteBuffer.allocate(bytes.length);
        allocate.put(bytes);
        allocate.flip();
        socketChannel.write(allocate);

        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        int read = socketChannel.read(readBuffer);
        // 将服务端发来的数据读到缓冲区
        if (read > 0) {
            // 切换读写模式
            readBuffer.flip();
            byte[] serverMessage = new byte[readBuffer.remaining()];
            readBuffer.get(serverMessage);
            String serverMsg = new String(serverMessage, Charset.defaultCharset());
            System.out.println("客户端接收到服务端消息：" + serverMsg);
            readBuffer.clear();
        }
        socketChannel.close();
    }
}
