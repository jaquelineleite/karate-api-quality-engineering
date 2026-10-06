package runners;

import com.intuit.karate.junit5.Karate;

public class SmokeRunner {

    @Karate.Test
    Karate testSmoke() {
        return Karate.run("classpath:features")
                .tags("@smoke");
    }
}
