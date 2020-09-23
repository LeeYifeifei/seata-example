package com.demo.samples.seata.controller;

import com.demo.samples.seata.dto.ProductReduceStockDTO;
import com.demo.samples.seata.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {


    @Autowired
    private ProductService productService;

    @PostMapping("/reduce-stock")
    public Double reduceStock(@RequestBody ProductReduceStockDTO productReduceStockDTO)
            throws Exception {
        return productService.reduceStock(productReduceStockDTO.getProductId(), productReduceStockDTO.getAmount());
    }

}