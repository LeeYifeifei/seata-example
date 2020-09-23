package com.demo.samples.seata.feign;

import com.demo.samples.seata.feign.dto.AccountReduceBalanceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * `account-service` 服务的 Feign 客户端
 */
@FeignClient(name = "account-service")
public interface AccountServiceFeignClient {

    /**
     * 扣减余额
     * @param accountReduceBalanceDTO
     */
    @PostMapping("/account/reduce-balance")
    void reduceBalance(@RequestBody AccountReduceBalanceDTO accountReduceBalanceDTO);

}
