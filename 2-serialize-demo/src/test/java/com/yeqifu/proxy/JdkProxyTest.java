package com.yeqifu.proxy;

import com.yeqifu.model.bo.Book;
import com.yeqifu.model.bo.BookService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author: yeqifu
 * @date: 2024/8/6 16:35
 */
public class JdkProxyTest {
    public static void main(String[] args) {
        // sun.misc.ProxyGenerator.saveGeneratedFiles = true 会将生成的代理类保存在工程根目录下的 com/sum/proxy
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        BookService bookService = (BookService) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{BookService.class},
                new Handler()
        );
        Book book = bookService.findBook("100");
        System.out.println(book);
    }

    static class Handler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("发起远程网络调用");
            return Book.builder().id((String) args[0])
                    .name("Kafka实战")
                    .title("IT")
                    .tag("消息引擎")
                    .content("kafka")
                    .build();
        }
    }
}
