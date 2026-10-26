package br.com.jaqueline.payment.service;

import br.com.jaqueline.payment.domain.PaymentRequest;
import br.com.jaqueline.payment.domain.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentService {

    private final ConcurrentHashMap<String, StoredPayment> payments =
            new ConcurrentHashMap<>();

    public PaymentResponse process(
            String idempotencyKey,
            String correlationId,
            PaymentRequest request) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "X-Idempotency-Key is required"
            );
        }

        if (request == null
                || request.bookingId() == null
                || request.amount() == null
                || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid payment request"
            );
        }

        StoredPayment stored = payments.compute(
                idempotencyKey,
                (key, existing) -> {

                    if (existing != null) {
                        if (!existing.bookingId().equals(request.bookingId())
                                || existing.amount().compareTo(request.amount()) != 0) {
                            throw new ResponseStatusException(
                                    HttpStatus.CONFLICT,
                                    "Idempotency key reused with different payload"
                            );
                        }
                        return existing;
                    }

                    return new StoredPayment(
                            UUID.randomUUID().toString(),
                            request.bookingId(),
                            request.amount(),
                            "APPROVED",
                            correlationId
                    );
                }
        );

        return new PaymentResponse(
                stored.paymentId(),
                stored.bookingId(),
                stored.status(),
                stored.correlationId()
        );
    }

    private record StoredPayment(
            String paymentId,
            Long bookingId,
            BigDecimal amount,
            String status,
            String correlationId
    ) {}
}
