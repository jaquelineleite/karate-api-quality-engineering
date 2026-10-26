package br.com.jaqueline.payment.controller;

import br.com.jaqueline.payment.domain.PaymentRequest;
import br.com.jaqueline.payment.domain.PaymentResponse;
import br.com.jaqueline.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(
            @RequestHeader(value = "X-Correlation-ID", required = false)
            String correlationId,
            @RequestHeader(value = "X-Idempotency-Key", required = false)
            String idempotencyKey,
            @RequestBody PaymentRequest request) {

        String effectiveCorrelationId =
                correlationId == null || correlationId.isBlank()
                        ? UUID.randomUUID().toString()
                        : correlationId;

        PaymentResponse response = paymentService.process(
                idempotencyKey,
                effectiveCorrelationId,
                request
        );

        return ResponseEntity.ok()
                .header("X-Correlation-ID", effectiveCorrelationId)
                .body(response);
    }
}
