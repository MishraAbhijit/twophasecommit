package com.abhijit.paymentservice.api;

import com.abhijit.paymentservice.dto.OrderRequest;
import com.abhijit.paymentservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/prepare")
    public ResponseEntity<String> prepare(@RequestBody OrderRequest request) {
        paymentService.preparePayment(request);
        return ResponseEntity.ok("Payment prepared");
    }

    @PostMapping("/commit")
    public ResponseEntity<String> commit(@RequestBody OrderRequest request) {
        paymentService.commitPayment(request);
        return ResponseEntity.ok("Payment committed");
    }

    @PostMapping("/rollback")
    public ResponseEntity<String> rollback(@RequestBody OrderRequest request) {
        paymentService.rollbackPayment(request);
        return ResponseEntity.ok("Payment rolled back");
    }
}
