package bg.bulsi.bfsa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({ "test", "h2", "h2-unit-test" })
class CoreApplicationTests {

	@Test
	void contextLoads() {
	}

}
