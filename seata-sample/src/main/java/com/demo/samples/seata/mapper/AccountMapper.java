package com.demo.samples.seata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.samples.seata.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

}