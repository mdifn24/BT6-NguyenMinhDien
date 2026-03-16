package com.example.demo.Security;

import com.example.demo.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // 1. Cấu hình mã hóa mật khẩu Bcrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Cấu hình Provider cung cấp dữ liệu cho Spring Security
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 3. Cấu hình phân quyền đường dẫn
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Cấp quyền cho thư mục chứa ảnh để ai cũng xem được hình sản phẩm
                        .requestMatchers("/images/**", "/css/**", "/js/**").permitAll()

                        // Quyền Thêm, Sửa, Xóa chỉ dành cho ADMIN
                        .requestMatchers("/products/create", "/products/edit/**", "/products/update", "/products/delete/**").hasRole("ADMIN")

                        // Mọi đường dẫn bắt đầu bằng /products yêu cầu phải đăng nhập
                        .requestMatchers("/products/**").authenticated()

                        // Các đường dẫn khác mở tự do
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        // Chuyển hướng về /products khi đăng nhập thành công
                        .defaultSuccessUrl("/products", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        // Khi không có quyền (Vd: USER cố tình truy cập /products/create)
                        .accessDeniedPage("/403")
                );

        return http.build();
    }
}