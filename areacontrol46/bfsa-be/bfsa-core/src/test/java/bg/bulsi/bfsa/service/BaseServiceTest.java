package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.Language;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
//@RunWith(SpringRunner.class)
@ActiveProfiles({ "test", "h2", "h2-unit-test" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseServiceTest {
    // base service test class

    protected static final String FAKE_VALUE = "fake";

    protected static final Language langBG = Language.builder().languageId(Language.BG).build();
    protected static final Language langEN = Language.builder().languageId(Language.EN).build();
}
