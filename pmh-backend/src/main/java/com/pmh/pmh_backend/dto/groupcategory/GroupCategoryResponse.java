package com.pmh.pmh_backend.dto.groupcategory;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter 
@Setter 
public class GroupCategoryResponse {
    private Long id;
    private String  paramName;
    private String paramValue;
    private String paramType;
    private String description;
    private String componentCode;
    private Integer status;
    private Integer isActive;
    private LocalDateTime effectiveDate;
    private LocalDateTime endEffectiveDate;

    private Object oldData;
    private Object newData;
    private List<String> changedFields;
}
