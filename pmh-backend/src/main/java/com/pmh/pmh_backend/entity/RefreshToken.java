package com.pmh.pmh_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table(name="REFRESH_TOKEN")
@Getter 
@Setter 
public class RefreshToken {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(length=1000, unique=true)
    private String token;

    private String username;
    private LocalDateTime expiryDate;
    private boolean revoked = false;
	
}
