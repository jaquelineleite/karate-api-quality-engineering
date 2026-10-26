package br.com.jaqueline.payment.controller;

import br.com.jaqueline.payment.domain.PaymentRequest;
import br.com.jaqueline.payment.domain.PaymentResponse;
import br.com.jaqueline.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${testing.delay.enabled:false}")
    private boolean testDelayEnabled;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(
            @RequestHeader(value = "X-Correlation-ID", required = false)
            String correlationId,
            @RequestHeader(value = "X-Idempotency-Key", required = false)
            String idempotencyKey,
            @RequestHeader(value = "X-Test-Delay-Ms", required = false)
            Long testDelayMs,
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

        if (testDelayEnabled && testDelayMs != null && testDelayMs > 0) {
            try {
                Thread.sleep(Math.min(testDelayMs, 10000L));
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(
                        "Simulacao de atraso interrompida", exception);
            }
        }

        return ResponseEntity.ok()
                .header("X-Correlation-ID", effectiveCorrelationId)
                .body(response);
    }
}
