package com.pmh.pmh_backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
 import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pmh.pmh_backend.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
@Configuration @RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true) // Bật @PreAuthorize trên Controller
public class SecurityConfig {
  private final JwtAuthenticationFilter filter; // Nhét Anh bảo vệ vào
  @Bean
  SecurityFilterChain chain(HttpSecurity h) throws Exception {
    h.csrf(c->c.disable()) // Tắt csrf vì dùng API, không dùng cookie form
     .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không lưu session, chỉ tin token
     .exceptionHandling(e -> e.authenticationEntryPoint(
    (req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
     .authorizeHttpRequests(a->a
       .requestMatchers("/api/auth/**").permitAll() // Cửa login/refresh cho vào tự do
       .anyRequest().authenticated()) // Còn lại (/api/components...) bắt buộc có Access hợp lệ
     .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class); // Gắn Bảo vệ đứng trước cửa
    return h.build();
  }
  @Bean PasswordEncoder encoder(){ return new BCryptPasswordEncoder(); } // Máy băm mật khẩu
}