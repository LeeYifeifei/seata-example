package com.demo.samples.seata.controller;

import com.demo.samples.seata.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Integer createOrder(@RequestParam("userId") Long userId,
                               @RequestParam("productId") Long productId,
                               @RequestParam("amount") Integer amount) throws Exception {
        log.info("[createOrder] 收到下单请求,用户:{}, 商品:{}, 数量:{}", userId, productId, amount);
        return orderService.createOrder(userId, productId, amount);
    }

}