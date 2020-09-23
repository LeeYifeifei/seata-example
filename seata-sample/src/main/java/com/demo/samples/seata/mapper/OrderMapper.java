package com.demo.samples.seata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.samples.seata.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}