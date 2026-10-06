package runners;

import com.intuit.karate.junit5.Karate;

public class BookingTest {

    @Karate.Test
    Karate testBooking() {
        return Karate.run("classpath:features/booking/booking.feature");
    }
}
