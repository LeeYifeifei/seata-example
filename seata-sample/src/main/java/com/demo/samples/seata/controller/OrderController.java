package com.demo.samples.seata.controller;

import com.demo.samples.seata.dto.PlaceOrderRequest;
import com.demo.samples.seata.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder")
    public String placeOrder(@Validated @RequestBody PlaceOrderRequest request) {
        orderService.placeOrder(request);
        return "下单成功";
    }
}