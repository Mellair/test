package com.mxy.regi.dto;

import com.mxy.regi.entity.Orders;
import com.mxy.regi.entity.OrderDetails;
import lombok.Data;
import java.util.List;

@Data
public class OrderDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetails> orderDetails;

}
