package br.com.jaqueline.booking.repository;

import br.com.jaqueline.booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
