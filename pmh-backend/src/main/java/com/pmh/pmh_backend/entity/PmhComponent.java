package com.pmh.pmh_backend.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table(name="PMH_COMPONENTS")
@EntityListeners(AuditingEntityListener.class)
@Getter 
@Setter 
public class PmhComponent {

    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="COMPONENT_CODE", length=20)
    private String componentCode;

    @Column(name="COMPONENT_NAME", length=150)
    private String componentName;

    @Column(name="MESSAGE_TYPE", length=1500)
    private String messageType;

    @Column(name="CONNECTION_METHOD", length=1000)
    private String connectionMethod;

    @Column(name="CHECK_TOKEN", length=2)
    private String checkToken;

    @Column(name="STATUS")
    private Integer status;

    @Column(name="IS_ACTIVE")
    private Integer isActive;

    @Column(name="IS_DISPLAY")
    private Integer isDisplay;

    @Column(name="NEW_DATA", length=4000)
    private String newData;

    @Column(name="EFFECTIVE_DATE")
    private LocalDateTime effectiveDate;

    @Column(name = "END_EFFECTIVE_DATE")
    private LocalDateTime endEffectiveDate;

    @CreatedBy 
    @Column(name="CREATED_BY", updatable=false)
    private String createdBy;

    @CreatedDate 
    @Column(name="CREATED_DATE", updatable=false)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name="UPDATED_BY")
    private String updatedBy;

    @LastModifiedDate
    @Column(name="UPDATED_DATE")
    private LocalDateTime updatedDate;
}
