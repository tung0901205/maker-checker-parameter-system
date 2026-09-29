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
@Getter 
@Setter 
@Table(name="PMH_HISTORY")
public class PmhHistory {
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="RECORD_ID")
    private Long recordId;

    @Column(name="TABLE_NAME")
    private String tableName;

    @Column(name="ACTION_TYPE")
    private String actionType;

    @Column(name="USERNAME")
    private String username;

    @Column(name="IP_ADDRESS")
    private String ipAddress;

    @Column(name = "ACTION_DATE")
    private LocalDateTime actionDate;

    @Column(name = "CONTENT", length = 4000)
    private String content;
}
