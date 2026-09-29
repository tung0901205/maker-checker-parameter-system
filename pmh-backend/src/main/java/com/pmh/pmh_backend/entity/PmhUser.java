package com.pmh.pmh_backend.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// Đổi APP_USER -> PMH_USERS cho đúng Oracle DEMO, thêm quan hệ qua PMH_USER_ROLE
@Entity
@Table(name = "PMH_USERS")
@Getter
@Setter
public class PmhUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USERNAME", unique = true, length = 50)
    private String username;

    @Column(name = "PASSWORD", length = 200)
    private String password; // Lưu BCrypt

    @ManyToMany(fetch = FetchType.EAGER) // EAGER để Filter đọc roles ngay không cần query thêm
    @JoinTable(
        name = "PMH_USER_ROLE", // Bảng trung gian đúng tên DEMO
        joinColumns = @JoinColumn(name = "USER_ID"),
        inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<PmhRole> roles = new HashSet<>(); // VD: maker có [MAKER], admin có [ADMIN]
}
