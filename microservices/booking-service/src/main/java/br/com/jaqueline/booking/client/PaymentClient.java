package br.com.jaqueline.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;

import java.math.BigDecimal;

@Component
public class PaymentClient {

    private final RestClient restClient;
    private final long testDelayMs;

    public PaymentClient(
            RestClient.Builder builder,
            @Value("${services.payment.url}") String paymentServiceUrl,
            @Value("${payment.test.delay.ms:0}") long testDelayMs) {

        this.testDelayMs = testDelayMs;

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = builder
                .baseUrl(paymentServiceUrl)
                .requestFactory(requestFactory)
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
                .header("X-Idempotency-Key", "booking-" + bookingId)
                .headers(headers -> {
                    if (testDelayMs > 0) {
                        headers.set("X-Test-Delay-Ms", String.valueOf(testDelayMs));
                    }
                })
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
