package com.godfathercapybara.capybara.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.godfathercapybara.capybara.security.jwt.JwtRequestFilter;
import com.godfathercapybara.capybara.security.jwt.UnauthorizedHandlerJwt;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
    public RepositoryUserDetailsService userDetailService;

	@Autowired
  	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

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
	@Order(1)
    public SecurityFilterChain apifilterChain(HttpSecurity http) throws Exception {
		http.authenticationProvider(authenticationProvider());
		http
			.securityMatcher("/api/**")
			.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));
		
		http
			.authorizeHttpRequests(authorize -> authorize
                    // PRIVATE ENDPOINTS
                    .requestMatchers(HttpMethod.GET,"/api/capybaras/*/analytics").hasAnyRole("USER","ADMIN")
					.requestMatchers(HttpMethod.POST,"/api/capybaras/*/sponsor").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE,"/api/capybaras/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PUT,"/api/capybaras/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST,"/api/capybaras/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,"/api/capybaras/**").permitAll()

					.requestMatchers(HttpMethod.POST,"/api/products/*/comments").hasAnyRole("USER","ADMIN")
                    .requestMatchers(HttpMethod.DELETE,"/api/products/*/comments/**").hasAnyRole("USER","ADMIN")
					.requestMatchers(HttpMethod.POST,"/api/products/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE,"/api/products/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,"/api/products/**").permitAll()

                    .requestMatchers(HttpMethod.POST,"/api/shops/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE,"/api/shops/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,"/api/shops/**").permitAll()

					.requestMatchers(HttpMethod.POST,"/api/auth/login").anonymous()
					.requestMatchers(HttpMethod.POST,"/api/auth/logout").authenticated()
					.requestMatchers(HttpMethod.POST,"/api/users/signup").anonymous()
					.requestMatchers(HttpMethod.GET,"/api/users").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,"/api/users/me").authenticated()
					.requestMatchers(HttpMethod.POST,"/api/auth/refresh").authenticated()
					.requestMatchers(HttpMethod.DELETE,"/api/users/*/delete").hasAnyRole("ADMIN","USER")
					.requestMatchers(HttpMethod.POST,"/api/users/**").authenticated()
			);
		
            // Disable Form login Authentication
            http.formLogin(formLogin -> formLogin.disable());

            // Disable CSRF protection (it is difficult to implement in REST APIs)
            http.csrf(csrf -> csrf.disable());

            // Disable Basic Authentication
            http.httpBasic(httpBasic -> httpBasic.disable());

            // Stateless session
            http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            // Add JWT Token filter
            http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
	}


	@Bean
	@Order(2)
	public SecurityFilterChain webfilterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http.authorizeHttpRequests(authorize -> authorize
					.requestMatchers("/users").hasRole("ADMIN")
					.requestMatchers("/users/**").authenticated()
					.requestMatchers("/me").authenticated()
					.requestMatchers("/capybaras/*/edit").hasRole("ADMIN")
					.requestMatchers("/capybaras/*/sponsor").hasRole("USER")
					.requestMatchers("/capybaras/*/delete").hasRole("ADMIN")
					.requestMatchers("/newcapybara").hasRole("ADMIN")
					.requestMatchers("/capybaras/*/analytics").hasAnyRole("USER","ADMIN")
					.requestMatchers("/capybaras/**").permitAll()
					.requestMatchers("/products/*/delete").hasRole("ADMIN")
					.requestMatchers("/products/*/comments/*/delete").hasAnyRole("USER","ADMIN")
					.requestMatchers("/products/*/newcomment").hasAnyRole("ADMIN", "USER")
					.requestMatchers("/products/**").permitAll()
					.requestMatchers("/shops/*/delete").hasRole("ADMIN")
					.requestMatchers("/newshop").hasRole("ADMIN")
					.requestMatchers("/shops/**").permitAll()
					.requestMatchers("/login").anonymous()
					.requestMatchers("/loginerror").anonymous()
					.requestMatchers("/signup").anonymous()
					.anyRequest().permitAll()
					

			);
	
		http.formLogin(formLogin -> formLogin
					.loginPage("/login")
					.failureUrl("/loginerror")
					.defaultSuccessUrl("/me")
					.permitAll()
			);
		
		http.logout(logout -> logout
					.logoutUrl("/logout")
					.logoutSuccessUrl("/")
					.permitAll()
			);
		
		return http.build();
	}

}
	

