package com.pmh.pmh_backend.dto.groupcategory;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class GroupCategorySearchRequest {

    private String paramType;
    private String paramValue;
    private String paramName;

    private List<Integer> statuses;
    private List<Integer> isActives;

    private int page = 0;
    private int size = 20;
}
