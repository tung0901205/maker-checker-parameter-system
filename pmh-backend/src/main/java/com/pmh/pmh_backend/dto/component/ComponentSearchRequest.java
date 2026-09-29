package com.pmh.pmh_backend.dto.component;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class ComponentSearchRequest {
    private List<String> componentCodes;
    private List<String> componentNames;
    private List<Integer> statuses;
    private List<Integer> isActives;

    private int page = 0;
    private int size = 20;
    
}
