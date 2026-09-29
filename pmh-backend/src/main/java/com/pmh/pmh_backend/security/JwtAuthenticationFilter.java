package com.pmh.pmh_backend.security;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pmh.pmh_backend.repository.AppUserRepository;

 import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
 import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
@Component @RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // Lọc 1 lần / 1 request
  private final JwtService jwt; private final AppUserRepository userRepo;

  @Override // Không lọc cửa login/refresh cho người ta vào
  protected boolean shouldNotFilter(HttpServletRequest req){ return req.getServletPath().startsWith("/api/auth"); }

  @Override // Chặn mọi request vào Controller để kiểm giấy
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
    String h = req.getHeader("Authorization"); // Tìm tờ giấy trong Header
    if(h != null && h.startsWith("Bearer ")){ // Đúng mẫu "Bearer <token>" không?
      String token = h.substring(7); // Cắt lấy chuỗi token
      if(jwt.isValid(token)){ // Đưa cho Thợ kiểm tra dấu + hạn
        String u = jwt.extractUser(token); // Bóc tên
        if(userRepo.findByUsername(u).isPresent()){ // User còn tồn tại trong DB không?
          // Đọc roles trong token -> authorities ROLE_XXX để @PreAuthorize dùng
          List<GrantedAuthority> authorities = jwt.extractRoles(token).stream()
              .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
              .collect(Collectors.toList());
          UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      }
    }
    chain.doFilter(req, res); // Cho đi tiếp (vào Controller hoặc bị chặn 403 ở Mảnh 3)
  }
}