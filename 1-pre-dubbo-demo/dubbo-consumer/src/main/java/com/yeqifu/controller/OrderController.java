package com.yeqifu.controller;

import com.yeqifu.OrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yeqifu
 * @date: 2024/7/27 23:33
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    /**
     * @DubboReference 注入的是远程实现，而 @Autowired 注入的是本地 IOC 的实现
     */
    @DubboReference
    private OrderService orderService;

    @GetMapping("/getOrder")
    public String getOrder(Long orderId) {
        return orderService.getOrder(orderId);
    }

}
