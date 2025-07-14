package com.abhijit.orderservice.api;

import com.abhijit.orderservice.dto.OrderRequest;
import com.abhijit.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/prepare")
    public ResponseEntity<String> prepare(@RequestBody OrderRequest request) {
        orderService.prepareOrder(request);
        return ResponseEntity.ok("Order prepared");
    }

    @PostMapping("/commit")
    public ResponseEntity<String> commit(@RequestBody OrderRequest request) {
        orderService.commitOrder(request);
        return ResponseEntity.ok("Order committed");
    }

    @PostMapping("/rollback")
    public ResponseEntity<String> rollback(@RequestBody OrderRequest request) {
        orderService.rollbackOrder(request);
        return ResponseEntity.ok("Order rolled back");
    }

}
