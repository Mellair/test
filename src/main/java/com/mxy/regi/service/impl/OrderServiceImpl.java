package com.mxy.regi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mxy.regi.entity.Orders;
import com.mxy.regi.mapper.OrderMapper;
import com.mxy.regi.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService{
}
