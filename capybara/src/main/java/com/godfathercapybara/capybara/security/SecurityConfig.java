package com.godfathercapybara.capybara.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@EnableWebMvc
public class SecurityConfig {

	@Autowired
    public RepositoryUserDetailsService userDetailService;

    @Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http.authorizeHttpRequests(authorize -> authorize
					.requestMatchers("/users").hasRole("ADMIN")
					.requestMatchers("/users/**").authenticated()
					.requestMatchers("/capybaras/*/edit").hasRole("ADMIN")
					.requestMatchers("/capybaras/*/delete").hasRole("ADMIN")
					.requestMatchers("/newcapybara").hasRole("ADMIN")
					.requestMatchers("/capybaras/*/analytics").hasRole("USER")
					.requestMatchers("/capybaras/**").permitAll()
					.requestMatchers("products/*/delete").hasRole("ADMIN")
					.requestMatchers("/products/*/comments/*/delete").hasAnyRole("USER")
					.requestMatchers("/products/*/newcomment").hasAnyRole("ADMIN", "USER")
					.requestMatchers("/products/**").permitAll()
					.requestMatchers("/shops/*/delete").hasRole("ADMIN")
					.requestMatchers("/newshop").hasRole("ADMIN")
					.requestMatchers("/shops/**").permitAll()
					.requestMatchers("/").permitAll()
					
					
			);
	
		http.formLogin(formLogin -> formLogin
					.loginPage("/login")
					.failureUrl("/loginerror")
					.defaultSuccessUrl("/")
					.permitAll()
			);
		
		http.logout(logout -> logout
					.logoutUrl("/logout")
					.logoutSuccessUrl("/")
					.permitAll()
			);
		
		// Disable CSRF at the moment
		http.csrf(csrf -> csrf.disable());

		return http.build();
	}

}
	

