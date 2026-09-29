package com.pmh.pmh_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// Bảng PMH_ROLES trong Oracle DEMO: ADMIN full, MAKER tạo/sửa/gửi, CHECKER duyệt/từ chối/hủy, VIEWER chỉ xem
@Entity
@Table(name = "PMH_ROLES")
@Getter
@Setter
public class PmhRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Oracle 12c+ hỗ trợ IDENTITY, khớp với các entity cũ
    private Long id;

    @Column(name = "ROLE_CODE", unique = true, length = 20)
    private String roleCode; // ADMIN, MAKER, CHECKER, VIEWER

    @Column(name = "ROLE_NAME", length = 100)
    private String roleName; // Tên tiếng Việt để hiển thị
}
