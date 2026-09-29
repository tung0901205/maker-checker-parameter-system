package com.pmh.pmh_backend.dto.groupcategory;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 

public class GroupCategorySaveRequest {

    @NotEmpty(message="Cấu phần xử lý bắt buộc")
    private List<String> componentCode;

    @NotBlank(message="Tên thành phần bắt buộc")
    @Size(max=255)
    private String paramName;

    @NotBlank(message="Giá trị bắt buộc")
    @Size(max=255)
    private String paramValue;

    @NotBlank(message="Danh mục nhóm bắt buộc")
    @Size(max=255)
    private String paramType;

    @Size(max=4000)
    private String description;

    @NotNull(message="Ngày hiệu lực bắt buộc")
    private LocalDateTime effectiveDate;
    private LocalDateTime endEffectiveDate;
    
}
