package bg.bulsi.bfsa.config;

import bg.bulsi.bfsa.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class SecurityConfig {

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOriginPattern("*");
//		configuration.setAllowedOrigins(List.of("http://127.0.0.1", "http://192.168.8.45", "http://85.187.246.13:8081", "http://151.251.244.106"));
		configuration.setAllowedMethods(List.of("HEAD", "COPY", "DELETE", "GET", "POST", "PUT", "REPORT", "SEARCH", "UPDATE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
//		configuration.setAllowedHeaders(List.of("DNT", "User-Agent", "X-Requested-With", "If-Modified-Since", "Cache-Control", "Content-Type", "Range"));
		/* If you are returning data through Response Headers, you need to specify them here. for example,
		some APIs are designed to return Authorization token after success /authentication through Response Headers.
		Thus, the related header needs to be exposed accordingly. <- setExposedHeaders **/
		configuration.setExposedHeaders(List.of("File-Name", "Content-Length", "Content-Range"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
//		source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return source;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// Spring Security 6.2.0-M1 will automatically enable .cors() if CorsConfigurationSource bean is present
				.cors(Customizer.withDefaults()) // by default uses a Bean with the name of corsConfigurationSource
//				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable)
//				.csrf((csrf) -> csrf.ignoringRequestMatchers("/token"))
				.authorizeHttpRequests((auth) -> auth
						.requestMatchers("/static/**", "/actuator/**", "/swagger-resources/**",
								"/swagger-ui/**", "/v3/api-docs/**", "/webjars/**",
								"/templates/**", "/reports/**", "/auth/**", "/h2-console/**").permitAll()
//						.requestMatchers("/user/**").hasAnyAuthority("USER")
//						.requestMatchers("/user/**").hasAnyAuthority("ADMIN")
						.anyRequest().authenticated()
				)
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//				.headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) //to make accessible h2 console, it works as frame
				.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

}
