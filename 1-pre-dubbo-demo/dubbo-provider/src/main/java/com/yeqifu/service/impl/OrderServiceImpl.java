package com.yeqifu.service.impl;

import com.yeqifu.OrderService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author: yeqifu
 * @date: 2024/7/27 23:26
 */
@DubboService
public class OrderServiceImpl implements OrderService {

    @Value("${dubbo.protocol.port}")
    private String rpcServicePort;

    @Override
    public String getOrder(Long orderId) {
        String result = "get order detail, orderId = " + orderId + ", rpcServicePort = " + rpcServicePort;
        System.out.println(Thread.currentThread().getName() + " --- " + result);
        return result;
    }
}
