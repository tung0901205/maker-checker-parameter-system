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
@Table(name="PMH_GROUP_CATEGORY") 
@EntityListeners(AuditingEntityListener.class)
@Getter 
@Setter 
public class PmhGroupCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="PARAM_NAME")
    private String paramName;

    @Column(name="PARAM_VALUE")
    private String paramValue;

    @Column(name="PARAM_TYPE")
    private String paramType;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="COMPONENT_CODE")
    private String componentCode;

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

    @Column(name="END_EFFECTIVE_DATE")
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
