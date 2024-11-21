package com.yeqifu;

/**
 * @author: yeqifu
 * @date: 2024/7/27 23:29
 */
public interface OrderService {

    /**
     * 根据订单 ID 获取订单信息
     *
     * @param orderId
     * @return
     */
    String getOrder(Long orderId);

}
