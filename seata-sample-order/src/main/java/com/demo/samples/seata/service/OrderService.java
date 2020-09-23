package com.demo.samples.seata.service;

/**
 * 订单 Service
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param userId 用户编号
     * @param productId 产品编号
     * @param amount 数量
     * @return 订单编号
     * @throws Exception 创建订单失败，抛出异常
     */
    Integer createOrder(Long userId, Long productId, Integer amount) throws Exception;
}