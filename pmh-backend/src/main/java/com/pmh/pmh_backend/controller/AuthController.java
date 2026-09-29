package com.pmh.pmh_backend.controller;
import java.util.Map;

 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pmh.pmh_backend.service.AuthService;

import lombok.RequiredArgsConstructor;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
  private final AuthService auth;
  @PostMapping("/login") public Map<String,String> login(@RequestBody Map<String,String> b){
    return auth.login(b.get("username"), b.get("password"));
  }
  @PostMapping("/refresh") public Map<String,String> refresh(@RequestBody Map<String,String> b){
    return auth.refresh(b.get("refreshToken"));
  }
  @PostMapping("/logout") public String logout(@RequestBody Map<String,String> b){
    auth.logout(b.get("refreshToken")); return "Logged out";
  }
}