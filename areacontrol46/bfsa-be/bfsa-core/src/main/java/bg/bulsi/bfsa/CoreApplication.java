package bg.bulsi.bfsa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableTransactionManagement
public class CoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}


//	@Profile("!noauth")
	@Configuration
	@EnableScheduling
	public class EnableSchedulingConfig {

	}

}
