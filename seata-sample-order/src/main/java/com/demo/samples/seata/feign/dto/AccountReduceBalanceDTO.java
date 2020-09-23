package com.demo.samples.seata.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账户减少余额 DTO
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountReduceBalanceDTO {

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 扣减金额
     */
    private Double price;

}
