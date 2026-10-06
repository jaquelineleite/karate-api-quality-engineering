package runners;

import com.intuit.karate.junit5.Karate;

public class AuthTest {

    @Karate.Test
    Karate testAuth() {
        return Karate.run("classpath:features/auth/auth.feature");
    }
}
