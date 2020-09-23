package com.demo.samples.seata.service.impl;

import com.demo.samples.seata.common.OrderStatus;
import com.demo.samples.seata.entity.Order;
import com.demo.samples.seata.feign.AccountServiceFeignClient;
import com.demo.samples.seata.feign.ProductServiceFeignClient;
import com.demo.samples.seata.feign.dto.AccountReduceBalanceDTO;
import com.demo.samples.seata.feign.dto.ProductReduceStockDTO;
import com.demo.samples.seata.mapper.OrderMapper;
import com.demo.samples.seata.service.OrderService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AccountServiceFeignClient accountService;

    @Autowired
    private ProductServiceFeignClient productService;

    @Override
    @GlobalTransactional
    public Integer createOrder(Long userId, Long productId, Integer amount) {
        log.info("=============ORDER START=================");
        log.info("收到下单请求,用户:{}, 商品:{},数量:{}", userId, productId, amount);
        log.info("[createOrder] 当前 XID: {}", RootContext.getXID());
        Order order = Order.builder()
                .userId(userId)
                .productId(productId)
                .status(OrderStatus.INIT)
                .amount(amount)
                .build();

        orderMapper.insert(order);
        log.info("订单一阶段生成，等待扣库存付款中");

        //手动制造异常
//        System.out.println(1/0);

        // 扣减库存，并计算总价
        Double totalPrice = productService.reduceStock(ProductReduceStockDTO.builder().productId(productId).amount(amount).build());

        // 扣减余额
        accountService.reduceBalance(AccountReduceBalanceDTO.builder().userId(userId).price(totalPrice).build());

        // 修改订单
        order.setStatus(OrderStatus.SUCCESS);
        order.setTotalPrice(totalPrice);
        orderMapper.updateById(order);
        log.info("订单已成功下单，订单编号："+order.getId());
        log.info("=============ORDER END=================");

        // 返回订单编号
        return order.getId();
    }

}