package com.pmh.pmh_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.pmh.pmh_backend.entity.PmhGroupCategory;

@Repository 

public interface GroupCategoryRepository extends JpaRepository<PmhGroupCategory, Long>, JpaSpecificationExecutor<PmhGroupCategory> {
    //check trung cap gia tri + dm nhom
    boolean existsByParamTypeAndParamValue(String paramType, String paramValue);

    //check trung khi sua
    boolean existsByParamTypeAndParamValueAndIdNot(String paramType, String paramValue, Long id);
}
