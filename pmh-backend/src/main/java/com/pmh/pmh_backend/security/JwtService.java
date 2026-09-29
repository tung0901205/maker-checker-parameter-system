package com.pmh.pmh_backend.security; // Nằm trong mục security
import java.nio.charset.StandardCharsets;
 import java.util.Date;

import javax.crypto.SecretKey;

 import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

 import io.jsonwebtoken.Jwts;
 import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {
  @Value("${jwt.secret}") private String secret;
  @Value("${jwt.access-exp}") private long accessExp;
  @Value("${jwt.refresh-exp}") private long refreshExp;

  private SecretKey getKey(){ // Dựng con dấu từ chuỗi secret
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }
  public String genAccess(String username){ // Giữ lại cho tương thích cũ
    return genAccess(username, java.util.List.of());
  }

  public String genAccess(String username, java.util.List<String> roles){ // Đóng dấu Access 15p + nhét roles
    return Jwts.builder().subject(username).claim("roles", roles).issuedAt(new Date())
      .expiration(new Date(System.currentTimeMillis()+accessExp))
      .signWith(getKey()).compact();
  }

  @SuppressWarnings("unchecked")
  public java.util.List<String> extractRoles(String token){ // Bóc roles trong ruột token
    Object r = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload().get("roles");
    if (r == null) return java.util.List.of();
    if (r instanceof java.util.List) return (java.util.List<String>) r;
    return java.util.List.of();
  }
  public String genRefresh(String username){ // Đóng dấu Refresh 7 ngày
    return Jwts.builder().subject(username).issuedAt(new Date())
      .expiration(new Date(System.currentTimeMillis()+refreshExp))
      .signWith(getKey()).compact();
  }
  public String extractUser(String token){ // Bóc username trong ruột token
    return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload().getSubject();
  }
  public boolean isValid(String token){ // Kiểm tra còn hạn + đúng con dấu không
    try{ extractUser(token); return true; } catch(Exception e){ return false; }
  }
}