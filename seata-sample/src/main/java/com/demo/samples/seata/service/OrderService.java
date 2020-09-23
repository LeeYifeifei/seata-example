package com.demo.samples.seata.service;

import com.demo.samples.seata.dto.PlaceOrderRequest;

public interface OrderService {

    /**
     * 下单
     *
     * @param placeOrderRequest 订单请求参数
     */
    void placeOrder(PlaceOrderRequest placeOrderRequest);
}