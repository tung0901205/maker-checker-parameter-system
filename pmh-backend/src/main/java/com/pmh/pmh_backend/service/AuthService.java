package com.pmh.pmh_backend.service;
import java.time.LocalDateTime;
import java.util.Map;
 
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional;

import com.pmh.pmh_backend.entity.PmhUser;
import com.pmh.pmh_backend.entity.RefreshToken;
import com.pmh.pmh_backend.repository.AppUserRepository;
import com.pmh.pmh_backend.repository.RefreshTokenRepository;
import com.pmh.pmh_backend.security.JwtService;

import lombok.RequiredArgsConstructor;
@Service @RequiredArgsConstructor
public class AuthService {
  private final AppUserRepository userRepo; private final RefreshTokenRepository reRepo;
  private final JwtService jwt; private final PasswordEncoder encoder;

  public Map<String,String> login(String u, String p){ // LOGIN: sinh acc (kèm roles) + re
    PmhUser user = userRepo.findByUsername(u).orElseThrow(()->new RuntimeException("Sai user"));
    if(!encoder.matches(p, user.getPassword())) throw new RuntimeException("Sai pass");
    java.util.List<String> roles = user.getRoles().stream().map(r -> r.getRoleCode()).toList(); // Lấy ADMIN/MAKER...
    String acc = jwt.genAccess(u, roles); String re = jwt.genRefresh(u);
    saveRe(u, re);
    return Map.of("accessToken", acc, "refreshToken", re);
  }
  @Transactional
  public Map<String,String> refresh(String oldRe){ // REFRESH: re -> acc mới + re mới
    if(!jwt.isValid(oldRe)) throw new RuntimeException("Refresh hết hạn/sai dấu");
    RefreshToken stored = reRepo.findByToken(oldRe).orElseThrow(()->new RuntimeException("Refresh lạ, không trong két"));
    if(stored.isRevoked()){ // HACKER SPAM LẠI CON ĐÃ DÙNG -> REUSE
      revokeAll(stored.getUsername()); // Khóa hết của user để bảo vệ
      throw new RuntimeException("Phát hiện dùng lại token cũ! Đã khóa hết, mời login lại");
    }
    stored.setRevoked(true); reRepo.save(stored); // Thu hồi con cũ ngay (xoay vòng)
    String u = stored.getUsername();
    // Lấy roles mới nhất từ DB để nhét vào acc mới (phòng khi admin đổi quyền giữa chừng)
    java.util.List<String> roles = userRepo.findByUsername(u).map(user -> user.getRoles().stream().map(r -> r.getRoleCode()).toList()).orElse(java.util.List.of());
    String newAcc = jwt.genAccess(u, roles); String newRe = jwt.genRefresh(u);
    saveRe(u, newRe); // Lưu con mới
    return Map.of("accessToken", newAcc, "refreshToken", newRe);
  }
  public void logout(String re){ // Đăng xuất: thu hồi luôn
    reRepo.findByToken(re).ifPresent(t->{t.setRevoked(true); reRepo.save(t);});
  }
  private void saveRe(String u, String token){
    RefreshToken r = new RefreshToken(); r.setToken(token); r.setUsername(u);
    r.setExpiryDate(LocalDateTime.now().plusDays(7)); r.setRevoked(false); reRepo.save(r);
  }
  private void revokeAll(String u){
    reRepo.findAllByUsernameAndRevokedFalse(u).forEach(t->{t.setRevoked(true); reRepo.save(t);});
  }
}