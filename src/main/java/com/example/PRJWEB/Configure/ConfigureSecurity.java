    package com.example.PRJWEB.Configure;

    import jakarta.servlet.http.HttpServletResponse;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
    import org.springframework.security.oauth2.jwt.JwtDecoder;
    import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
    import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
    import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
    import org.springframework.security.web.SecurityFilterChain;

    import javax.crypto.spec.SecretKeySpec;

    @Slf4j
    @Configuration
    @EnableWebSecurity
    @EnableMethodSecurity
    public class ConfigureSecurity {

        @Value("${spring.security.jwt.signer-key}")
        private String SIGNER_KEY;
        private final String[] PUBLIC_ENDPOINT = {"users/customers", "/auth/token", "/auth/introspect" ,"/tours" };

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
            http
                    .cors(Customizer.withDefaults())
                    .authorizeHttpRequests(request ->
                    request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll()
                            .anyRequest().authenticated());

            http.oauth2ResourceServer(oauth ->
                    oauth.jwt(jwtConfigurer ->
                                    jwtConfigurer.decoder(jwtDecoder())
                                            .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                            .authenticationEntryPoint(new jwtAuthenticationEntryPoint())
            );

        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());

            http.exceptionHandling(exception ->
                    exception.accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("❌ Access Denied: " + accessDeniedException.getMessage());
                        log.error("🔥 Access Denied: {}", accessDeniedException.getMessage());
                    })
            );
        return http.build();
        }

        @Bean
        JwtDecoder jwtDecoder(){
            SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(),"HS512");
            return NimbusJwtDecoder.
                    withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        @Bean
        JwtAuthenticationConverter jwtAuthenticationConverter(){
            JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =new JwtGrantedAuthoritiesConverter();
            jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
            JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
            return jwtAuthenticationConverter;
        }

        @Bean
        public PasswordEncoder passwordEncoder(){
            return new BCryptPasswordEncoder();
        }
    }
