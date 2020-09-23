package com.demo.samples.seata.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品减少库存 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReduceStockDTO {

    /**
     * 商品编号
     */
    private Long productId;
    /**
     * 数量
     */
    private Integer amount;

}
