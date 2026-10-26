package br.com.jaqueline.payment.service;

import br.com.jaqueline.payment.domain.PaymentRequest;
import br.com.jaqueline.payment.domain.PaymentResponse;
import br.com.jaqueline.payment.persistence.PaymentEntity;
import br.com.jaqueline.payment.persistence.PaymentRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

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

        PaymentEntity existing = repository
                .findByIdempotencyKey(idempotencyKey)
                .orElse(null);

        if (existing != null) {
            return validateAndRespond(existing, request);
        }

        PaymentEntity payment = new PaymentEntity(
                UUID.randomUUID().toString(),
                idempotencyKey,
                request.bookingId(),
                request.amount(),
                "APPROVED",
                correlationId
        );

        try {
            PaymentEntity saved = repository.saveAndFlush(payment);
            return toResponse(saved);

        } catch (DataIntegrityViolationException exception) {

            PaymentEntity stored = repository
                    .findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> exception);

            return validateAndRespond(stored, request);
        }
    }

    private PaymentResponse validateAndRespond(
            PaymentEntity stored,
            PaymentRequest request) {

        if (!stored.getBookingId().equals(request.bookingId())
                || stored.getAmount().compareTo(request.amount()) != 0) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Idempotency key reused with different payload"
            );
        }

        return toResponse(stored);
    }

    private PaymentResponse toResponse(PaymentEntity payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getBookingId(),
                payment.getStatus(),
                payment.getCorrelationId()
        );
    }
}
