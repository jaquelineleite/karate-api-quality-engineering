package runners;

import com.intuit.karate.junit5.Karate;

public class ContractTest {

    @Karate.Test
    Karate testContracts() {
        return Karate.run("classpath:features/contracts/booking-contract.feature");
    }
}
