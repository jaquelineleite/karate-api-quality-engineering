package br.com.jaqueline.payment.controller;

import br.com.jaqueline.payment.domain.PaymentRequest;
import br.com.jaqueline.payment.domain.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentController.class);

    @PostMapping
    public ResponseEntity<PaymentResponse> create(
            @RequestHeader(value = "X-Correlation-ID", required = false)
            String correlationId,
            @RequestBody PaymentRequest request) {

        String effectiveCorrelationId =
                correlationId == null || correlationId.isBlank()
                        ? UUID.randomUUID().toString()
                        : correlationId;

        String paymentId = UUID.randomUUID().toString();

        log.info(
                "Processing payment paymentId={} bookingId={} correlationId={}",
                paymentId,
                request.bookingId(),
                effectiveCorrelationId
        );

        PaymentResponse response = new PaymentResponse(
                paymentId,
                request.bookingId(),
                "APPROVED",
                effectiveCorrelationId
        );

        log.info(
                "Payment approved paymentId={} bookingId={} correlationId={}",
                paymentId,
                request.bookingId(),
                effectiveCorrelationId
        );

        return ResponseEntity.ok()
                .header("X-Correlation-ID", effectiveCorrelationId)
                .body(response);
    }
}
