package org.example.controller;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.example.service.Orderservice;


@RestController
@Slf4j
//京东订单
public class OrderController {

    @Resource
    private Orderservice orderservice;

    @RequestMapping(value = "/order/add",method = RequestMethod.POST)
    public void addOrder() {
        orderservice.addOrder();
    }

    @RequestMapping(value = "/order/{keyId}",method = RequestMethod.GET)
    public void getOrder(@PathVariable Integer keyId) {
        orderservice.getOrderByid(keyId);
    }

}
