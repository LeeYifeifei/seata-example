package com.demo.samples.seata.feign;

import com.demo.samples.seata.feign.dto.ProductReduceStockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * `product-service` 服务的 Feign 客户端
 */
@FeignClient(name = "product-service")
public interface ProductServiceFeignClient {

    /**
     * 扣减库存，并计算总价
     * @param productReduceStockDTO
     * @return
     */
    @PostMapping("/product/reduce-stock")
    Double reduceStock(@RequestBody ProductReduceStockDTO productReduceStockDTO);

}
