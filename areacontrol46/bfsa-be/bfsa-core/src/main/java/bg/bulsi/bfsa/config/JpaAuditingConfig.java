package bg.bulsi.bfsa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableEnversRepositories(basePackages = {"bg.bulsi.bfsa.repository"})
public class JpaAuditingConfig {

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication() != null
				? SecurityContextHolder.getContext().getAuthentication().getName()
				: null);
	}

}
