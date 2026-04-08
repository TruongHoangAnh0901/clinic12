package com.clinic.nhom12.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import com.clinic.nhom12.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean mã hoá mật khẩu bằng BCrypt — được inject vào UserService
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    @Lazy
    private UserService userService;

    /**
     * Cấu hình bảo mật cho các HTTP request:
     * - Static files (html, css, js) được truy cập tự do
     * - API đăng nhập/đăng ký tự do
     * - Các API còn lại yêu cầu xác thực theo role
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Tắt CSRF vì dùng REST API với frontend tách biệt
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // Cho phép toàn bộ static files (HTML, CSS, JS, images)
                .requestMatchers(
                    "/", "/index.html", "/login.html", "/register.html",
                    "/css/**", "/js/**", "/images/**", "/uploads/**",
                    "/*.html", "/*.css", "/*.js"
                ).permitAll()

                // Cho phép API đăng nhập, upload và các API danh mục/nghiệp vụ (Tạm thời mở vì Frontend dùng Sessionless LocalStorage)
                .requestMatchers("/api/auth/**", "/api/upload", "/api/chat").permitAll()
                .requestMatchers(HttpMethod.GET, 
                    "/api/medicines", "/api/medicines/**", 
                    "/api/medical-services", "/api/medical-services/**", 
                    "/api/specialties", "/api/specialties/**", 
                    "/api/appointments", "/api/appointments/**", 
                    "/api/medical-records", "/api/medical-records/**", 
                    "/api/doctors", "/api/doctors/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, 
                    "/api/medicines", "/api/medicines/**", 
                    "/api/medical-services", "/api/medical-services/**", 
                    "/api/specialties", "/api/specialties/**", 
                    "/api/appointments", "/api/appointments/**", 
                    "/api/medical-records", "/api/medical-records/**", 
                    "/api/doctors", "/api/doctors/**"
                ).permitAll()
                .requestMatchers(HttpMethod.PUT, 
                    "/api/medicines", "/api/medicines/**", 
                    "/api/medical-services", "/api/medical-services/**", 
                    "/api/specialties", "/api/specialties/**", 
                    "/api/appointments", "/api/appointments/**", 
                    "/api/medical-records", "/api/medical-records/**", 
                    "/api/doctors", "/api/doctors/**"
                ).permitAll()
                .requestMatchers(HttpMethod.DELETE, 
                    "/api/medicines", "/api/medicines/**", 
                    "/api/medical-services", "/api/medical-services/**", 
                    "/api/specialties", "/api/specialties/**", 
                    "/api/appointments", "/api/appointments/**", 
                    "/api/medical-records", "/api/medical-records/**", 
                    "/api/doctors", "/api/doctors/**"
                ).permitAll()


                .anyRequest().authenticated()
            )

            // Cấu hình OAuth2 Đăng nhập bằng Google
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login.html")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                            Authentication authentication) throws IOException {
                        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                        String email = oauthUser.getAttribute("email");
                        String name = oauthUser.getAttribute("name");
                        
                        // Lưu thông tin người dùng vào database
                        com.clinic.nhom12.entity.User user = userService.processOAuthPostLogin(email, name);
                        
                        // Chuyển hướng tới API xử lý để lưu vào localStorage
                        response.sendRedirect("/api/auth/oauth2-success?userId=" + user.getId() + "&fullName=" + URLEncoder.encode(name, "UTF-8") + "&role=ROLE_PATIENT");
                    }
                })
            )

            // Tắt form login mặc định của Spring Security
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
