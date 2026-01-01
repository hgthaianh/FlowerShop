package vn.quahoa.flowershop;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Requires full database connection - run manually")
class FlowerShopApplicationTests {

    @Test
    void contextLoads() {
        // This test simply verifies that the Spring application context loads
        // successfully
        // Disabled by default as it requires PostgreSQL to be running
    }
}
