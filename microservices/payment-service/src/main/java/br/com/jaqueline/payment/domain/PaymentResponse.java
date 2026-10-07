package br.com.jaqueline.payment.domain;

public record PaymentResponse(
        String paymentId,
        Long bookingId,
        String status,
        String correlationId
) {
}
