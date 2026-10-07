package br.com.jaqueline.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(
            RestClient.Builder builder,
            @Value("${services.payment.url}") String paymentServiceUrl) {

        this.restClient = builder
                .baseUrl(paymentServiceUrl)
                .build();
    }

    public PaymentResponse processPayment(
            Long bookingId,
            BigDecimal amount,
            String correlationId) {

        PaymentRequest request =
                new PaymentRequest(bookingId, amount);

        return restClient.post()
                .uri("/payments")
                .header("X-Correlation-ID", correlationId)
                .body(request)
                .retrieve()
                .body(PaymentResponse.class);
    }

    public record PaymentRequest(
            Long bookingId,
            BigDecimal amount) {
    }

    public record PaymentResponse(
            String paymentId,
            Long bookingId,
            String status,
            String correlationId) {
    }
}
