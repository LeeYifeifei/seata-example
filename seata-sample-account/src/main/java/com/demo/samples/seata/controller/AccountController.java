package com.demo.samples.seata.controller;

import com.demo.samples.seata.dto.AccountReduceBalanceDTO;
import com.demo.samples.seata.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * 扣减余额
     * @param accountReduceBalanceDTO
     * @throws Exception
     */
    @PostMapping("/reduce-balance")
    public void reduceBalance(@RequestBody AccountReduceBalanceDTO accountReduceBalanceDTO) throws Exception {
        accountService.reduceBalance(accountReduceBalanceDTO.getUserId(), accountReduceBalanceDTO.getPrice());
    }

}