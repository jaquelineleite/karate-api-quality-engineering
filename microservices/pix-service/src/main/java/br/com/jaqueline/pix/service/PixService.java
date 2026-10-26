package br.com.jaqueline.pix.service;

import br.com.jaqueline.pix.domain.PixRequest;
import br.com.jaqueline.pix.domain.PixResponse;
import br.com.jaqueline.pix.persistence.PixTransactionEntity;
import br.com.jaqueline.pix.persistence.PixTransactionRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class PixService {

    private final PixTransactionRepository repository;

    public PixService(PixTransactionRepository repository) {
        this.repository = repository;
    }

    public PixResponse create(
            String idempotencyKey,
            String correlationId,
            PixRequest request) {

        validate(idempotencyKey, request);

        Optional<PixTransactionEntity> existing =
                repository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            return validateAndRespond(existing.get(), request);
        }

        PixTransactionEntity transaction = new PixTransactionEntity(
                UUID.randomUUID().toString(),
                idempotencyKey,
                request.payerAccount(),
                request.receiverPixKey(),
                request.amount(),
                request.description(),
                "PENDING",
                correlationId
        );

        try {
            return toResponse(repository.saveAndFlush(transaction));

        } catch (DataIntegrityViolationException exception) {

            PixTransactionEntity stored = repository
                    .findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> exception);

            return validateAndRespond(stored, request);
        }
    }

    public PixResponse findById(String transactionId) {

        PixTransactionEntity transaction = repository
                .findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "PIX transaction not found"
                ));

        return toResponse(transaction);
    }

    private void validate(String idempotencyKey, PixRequest request) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "X-Idempotency-Key is required"
            );
        }

        if (request == null
                || request.payerAccount() == null
                || request.payerAccount().isBlank()
                || request.receiverPixKey() == null
                || request.receiverPixKey().isBlank()
                || request.amount() == null
                || request.amount().compareTo(BigDecimal.ZERO) <= 0
                || request.amount().scale() > 2) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid PIX transfer request"
            );
        }
    }

    private PixResponse validateAndRespond(
            PixTransactionEntity stored,
            PixRequest request) {

        boolean differentPayload =
                !stored.getPayerAccount().equals(request.payerAccount())
                || !stored.getReceiverPixKey().equals(request.receiverPixKey())
                || stored.getAmount().compareTo(request.amount()) != 0
                || !Objects.equals(
                        stored.getDescription(),
                        request.description()
                );

        if (differentPayload) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Idempotency key reused with different payload"
            );
        }

        return toResponse(stored);
    }

    private PixResponse toResponse(PixTransactionEntity transaction) {

        return new PixResponse(
                transaction.getTransactionId(),
                transaction.getPayerAccount(),
                transaction.getReceiverPixKey(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getCorrelationId()
        );
    }
}
