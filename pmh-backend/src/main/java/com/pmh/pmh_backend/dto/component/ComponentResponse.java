package com.pmh.pmh_backend.dto.component;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter 
@Setter 

public class ComponentResponse {
    private Long id;
    private String componentCode;
    private String componentName;
    private String messageType;
    private String connectionMethod;
    private String checkToken;
    private Integer status;
    private Integer isActive;
    private LocalDateTime effectiveDate;
    private LocalDateTime endEffectiveDate;

    private Object oldData; 
    private Object newData; 
    private List<String> changedFields;
}
