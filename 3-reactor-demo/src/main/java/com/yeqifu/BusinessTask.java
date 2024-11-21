package com.yeqifu;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author: yeqifu
 * @date: 2024/8/8 23:16
 */
public class BusinessTask implements Runnable{

    private ByteBuffer buffer;

    private final SocketChannel socketChannel;

    public BusinessTask(ByteBuffer readBuffer, SocketChannel socketChannel) {
        this.buffer = readBuffer;
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        buffer = null;
        // decode
        String msg = new String(bytes, Charset.defaultCharset());
        System.out.println("服务端收到了客户端的数据：" + msg);
        // 进行业务处理...

        // 向客户端写数据
        byte[] bytes1 = "hello, reactor client\n".getBytes(StandardCharsets.UTF_8);
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes1.length);
        writeBuffer.put(bytes1);
        writeBuffer.flip();
        try {
            socketChannel.write(writeBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
