package com.mxy.regi.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mxy.regi.common.JsonResult;
import com.mxy.regi.entity.Orders;
import com.mxy.regi.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    /**
     * 后台显示订单信息
     */
    @GetMapping("/page")
    public JsonResult<Page> page(int page, int pageSize, Long number, String beginTime, String endTime) {
        log.info("page={},pageSize={},number={},beginTime={},endTime={}",page,pageSize,number,beginTime,endTime);
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //链式编程写查询条件
        queryWrapper.like(number!=null, Orders::getNumber,number)
                //前面加上判定条件是十分必要的，用户没有填写该数据，查询条件上就不添加它
                .gt(StringUtils.isNotBlank(beginTime), Orders::getOrderTime,beginTime)//大于起始时间
                .lt(StringUtils.isNotBlank(endTime), Orders::getOrderTime,endTime);//小于结束时间
        orderService.page(pageInfo,queryWrapper);
        return JsonResult.success(pageInfo);
    }

}
