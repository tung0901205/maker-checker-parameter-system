package com.pmh.pmh_backend.dto.component;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter 
@Setter 
public class ComponentSaveRequest {

    @NotBlank(message="Mã cấu phần bắt buộc")
    @Size(max=20)
    @Pattern(regexp="^[A-Z0-9_]+$", message="Chỉ chữ HOA, cấm TV, khoảng trắng, ký tự đặc biệt") 
    private String componentCode;

    @NotBlank(message="Tên cấu phần bắt buộc")
    @Size(max=150)
    private String componentName;

    private List<String> messageType;
    private List<String> connectionMethod;
    private String checkToken;

    @NotNull(message="Ngày hiệu lực bắt buộc")
    private LocalDateTime effectiveDate;
    private LocalDateTime endEffectiveDate;
    
}
