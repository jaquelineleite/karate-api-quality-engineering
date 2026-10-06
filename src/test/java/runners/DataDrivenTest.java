package runners;

import com.intuit.karate.junit5.Karate;

public class DataDrivenTest {

    @Karate.Test
    Karate testDataDriven() {
        return Karate.run("classpath:features/datadriven/booking-datadriven.feature");
    }
}
