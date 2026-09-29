package com.pmh.pmh_backend.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.pmh.pmh_backend.entity.PmhComponent;

public class ComponentSpecification {
    //1. loc ma
    public static Specification<PmhComponent> hasComponentCodes(List<String> codes){
        return (root, query, cb) -> {
            if(codes == null || codes.isEmpty()) return null;
            return root.get("componentCode").in(codes);
        };
    }

    //2. loc ten
    public static Specification<PmhComponent> hasComponentNames(List<String> names){
        return(root, query, cb) ->{
            if(names == null || names.isEmpty()) return null;
            return root.get("componentName").in(names);
        };
    }

    //3. loc trang thai
    public static Specification<PmhComponent> hasStatuses(List<Integer> statuses){
        return(root, query, cb) ->{
            if(statuses == null || statuses.isEmpty()) return null;
            return root.get("status").in(statuses);
        };
    }
    //4. loc trang thai hd
    public static Specification<PmhComponent> hasIsActives(List<Integer> isActives){
        return(root, query, cb) ->{
            if(isActives == null || isActives.isEmpty()) return null;
            return root.get("isActive").in(isActives);
        };
    }
}
