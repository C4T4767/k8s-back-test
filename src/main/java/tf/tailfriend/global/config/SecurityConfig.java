package tf.tailfriend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;
    private final OAuth2SuccessHandler successHandler;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthFilter,
            JwtAuthenticationEntryPoint jwtAuthEntryPoint,
            OAuth2SuccessHandler successHandler,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.successHandler = successHandler;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable()) // 폼 로그인 제거
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 제거
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    // APP
                    .requestMatchers("/api/oauth2/authorization/**").permitAll()
                    .requestMatchers("/api/**").permitAll()
                    // 공개 API
                    .requestMatchers("/login").permitAll()
                    .requestMatchers("/admin").permitAll()
                    // admin API
                    .requestMatchers("/api/admin/login", "/api/admin/auth/validate", "/api/admin/logout").permitAll()
                    .requestMatchers("/api/admin/register").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")

                    // 나머지 API 권한 설정
                    .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/api/oauth2/authorization") // ✅ 여기서 경로 커스터마이징
                        )
                        .successHandler(successHandler) // OAuth2 로그인 성공 후 핸들러 설정
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint)); // 인증 실패시 처리

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://tailfriends.kro.kr");
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
