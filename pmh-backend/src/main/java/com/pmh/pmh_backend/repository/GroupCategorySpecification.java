package com.pmh.pmh_backend.repository;

import com.pmh.pmh_backend.entity.PmhGroupCategory;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public class GroupCategorySpecification {
    // 1. Tìm mờ Danh mục nhóm
    public static Specification<PmhGroupCategory> paramTypeLike(String paramType) {
        return (root, query, cb) -> {
            if (paramType == null || paramType.isBlank()) return null;
            return cb.like(cb.lower(root.get("paramType")), "%" + paramType.trim().toLowerCase() + "%"); // lower để không phân biệt HOA/thường
        };
    }
    // 2. Tìm mờ Giá trị TP
    public static Specification<PmhGroupCategory> paramValueLike(String paramValue) {
        return (root, query, cb) -> {
            if (paramValue == null || paramValue.isBlank()) return null;
            return cb.like(cb.lower(root.get("paramValue")), "%" + paramValue.trim().toLowerCase() + "%");
        };
    }
    // 3. Tìm mờ Tên TP
    public static Specification<PmhGroupCategory> paramNameLike(String paramName) {
        return (root, query, cb) -> {
            if (paramName == null || paramName.isBlank()) return null;
            return cb.like(cb.lower(root.get("paramName")), "%" + paramName.trim().toLowerCase() + "%");
        };
    }
    // 4. Lọc nhiều Trạng thái
    public static Specification<PmhGroupCategory> hasStatuses(List<Integer> statuses) {
        return (root, query, cb) -> {
            if (statuses == null || statuses.isEmpty()) return null;
            return root.get("status").in(statuses);
        };
    }
    // 5. Lọc Trạng thái HĐ 
    public static Specification<PmhGroupCategory> hasIsActives(List<Integer> isActives) {
        return (root, query, cb) -> {
            if (isActives == null || isActives.isEmpty()) return null;
            return root.get("isActive").in(isActives);
        };
    }
}