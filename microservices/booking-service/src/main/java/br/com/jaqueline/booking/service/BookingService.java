package br.com.jaqueline.booking.service;

import br.com.jaqueline.booking.client.PaymentClient;
import br.com.jaqueline.booking.domain.Booking;
import br.com.jaqueline.booking.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookingService {

    private static final Logger log =
            LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository repository;
    private final PaymentClient paymentClient;

    public BookingService(
            BookingRepository repository,
            PaymentClient paymentClient) {
        this.repository = repository;
        this.paymentClient = paymentClient;
    }

    public Booking create(Booking booking) {

        booking.setStatus("PENDING");
        Booking created = repository.save(booking);

        log.info(
                "Calling payment-service bookingId={} correlationId={}",
                created.getId(),
                created.getCorrelationId()
        );

        try {

            PaymentClient.PaymentResponse payment =
                    paymentClient.processPayment(
                            created.getId(),
                            created.getTotalPrice(),
                            created.getCorrelationId()
                    );

            log.info(
                    "Payment response bookingId={} paymentId={} status={} correlationId={}",
                    created.getId(),
                    payment.paymentId(),
                    payment.status(),
                    payment.correlationId()
            );

            if ("APPROVED".equals(payment.status())) {
                created.setStatus("CONFIRMED");
            } else {
                created.setStatus("PAYMENT_FAILED");
            }

        } catch (Exception exception) {

            log.error(
                    "Payment service unavailable bookingId={} correlationId={} error={}",
                    created.getId(),
                    created.getCorrelationId(),
                    exception.getMessage()
            );

            created.setStatus("PAYMENT_FAILED");
        }

        return repository.save(created);
    }

    public Optional<Booking> findById(Long id) {
        return repository.findById(id);
    }
}
