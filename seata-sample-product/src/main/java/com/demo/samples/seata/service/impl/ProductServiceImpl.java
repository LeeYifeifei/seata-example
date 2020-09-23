package com.demo.samples.seata.service.impl;

import com.demo.samples.seata.entity.Product;
import com.demo.samples.seata.mapper.ProductMapper;
import com.demo.samples.seata.service.ProductService;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {


    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional//(propagation = Propagation.REQUIRES_NEW) // 开启新事物
    public Double reduceStock(Long productId, Integer amount) throws Exception {
        log.info("=============PRODUCT START=================");
        log.info("[reduceStock] 收到减少库存请求, 商品:{}, 数量:{}", productId, amount);
        log.info("[reduceStock] 当前 XID: {}", RootContext.getXID());

        // 检查库存
        Product product = productMapper.selectById(productId);
        Integer stock = product.getStock();
        log.info("商品编号为 {} 的库存为{},订单商品数量为{}", productId, stock, amount);

        if (stock < amount) {
            log.warn("商品编号为{} 库存不足，当前库存:{}", productId, stock);
            throw new RuntimeException("库存不足");
        }
        log.info("开始扣减商品编号为 {} 库存,单价商品价格为{}", productId, product.getPrice());

        // 扣减库存
        int currentStock = stock - amount;
        product.setStock(currentStock);
        productMapper.updateById(product);
        double totalPrice = product.getPrice() * amount;
        log.info("扣减商品编号为 {} 库存成功,扣减后库存为{}, {} 件商品总价为 {} ", productId, currentStock, amount, totalPrice);
        log.info("=============PRODUCT END=================");

        //手动制造异常
//        System.out.println(1/0);

        return totalPrice;
    }
}