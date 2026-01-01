package vn.quahoa.flowershop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import vn.quahoa.flowershop.security.RestAuthenticationEntryPoint;
import vn.quahoa.flowershop.security.TokenAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(restAuthenticationEntryPoint()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/",
                                "/error",
                                "/favicon.ico")
                        .permitAll()
                        .requestMatchers("/api/auth/**", "/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/api/products/**", "/api/categories/**").permitAll() // Public view of
                                                                                               // products
                        .requestMatchers("/api/blogs", "/api/blogs/**").permitAll() // Public view of blogs
                        .requestMatchers("/uploads/**", "/images/**").permitAll() // Static resources
                        .requestMatchers("/api/admins/**").permitAll() // Legacy admin Access (Insecure but required for
                                                                       // continuity)
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth
                                .baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository()))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                                .oidcUserService(customOidcUserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler));

        // Add our custom Token based authentication filter
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @org.springframework.beans.factory.annotation.Autowired
    private vn.quahoa.flowershop.security.oauth2.CustomOAuth2UserService customOAuth2UserService;

    @org.springframework.beans.factory.annotation.Autowired
    private vn.quahoa.flowershop.security.oauth2.CustomOidcUserService customOidcUserService;

    @org.springframework.beans.factory.annotation.Autowired
    private vn.quahoa.flowershop.security.oauth2.OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @org.springframework.beans.factory.annotation.Autowired
    private vn.quahoa.flowershop.security.oauth2.OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public vn.quahoa.flowershop.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new vn.quahoa.flowershop.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository();
    }
}
