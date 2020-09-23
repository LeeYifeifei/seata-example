package com.demo.samples.seata.service.impl;

import com.demo.samples.seata.entity.Account;
import com.demo.samples.seata.mapper.AccountMapper;
import com.demo.samples.seata.service.AccountService;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {


    @Autowired
    private AccountMapper accountMapper;

    @Override
    @Transactional//(propagation = Propagation.REQUIRES_NEW) // 开启新事物
    public void reduceBalance(Long userId, Integer price) throws Exception {
        log.info("=============ACCOUNT START=================");
        log.info("[reduceBalance] 收到减少余额请求, 用户:{}, 金额:{}", userId, price);
        log.info("[reduceBalance] 当前 XID: {}", RootContext.getXID());

        // 检查余额
        Account account = accountMapper.selectById(userId);
        Double balance = account.getBalance();
        log.info("下单用户{}余额为 {},商品总价为{}", userId, balance, price);

        if (balance < price) {
            log.warn("用户 {} 余额不足，当前余额:{}", userId, balance);
            throw new RuntimeException("余额不足");
        }
        // 扣除余额
        log.info("开始扣减用户 {} 余额", userId);
        double currentBalance = account.getBalance() - price;
        account.setBalance(currentBalance);
        accountMapper.updateById(account);
        log.info("扣减用户 {} 余额成功,扣减后用户账户余额为{}", userId, currentBalance);
        log.info("=============ACCOUNT END=================");

        //手动制造异常
//        System.out.println(1/0);
    }
}