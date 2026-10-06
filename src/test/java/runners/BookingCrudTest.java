package runners;

import com.intuit.karate.junit5.Karate;

public class BookingCrudTest {

    @Karate.Test
    Karate testBookingCrud() {
        return Karate.run("classpath:features/booking/booking-crud.feature");
    }
}
