package com.pmh.pmh_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pmh.pmh_backend.entity.PmhComponent;

@Repository
public interface ComponentRepository extends JpaRepository<PmhComponent, Long>, JpaSpecificationExecutor<PmhComponent> {
    boolean existsByComponentCode(String componentCode);

    @Query("SELECT DISTINCT c.componentCode FROM PmhComponent c WHERE c.componentCode IS NOT NULL ORDER BY c.componentCode")
    List<String> findDistinctCodes();

    @Query("SELECT DISTINCT c.componentName FROM PmhComponent c WHERE c.componentName IS NOT NULL ORDER BY c.componentName")
    List<String> findDistinctNames();

    // Dropdown "Cấu phần xử lý" cho màn Group Category - chỉ lấy cấu phần đang Hoạt động (PDF: IS_ACTIVE = 1)
    @Query("SELECT DISTINCT c.componentCode FROM PmhComponent c WHERE c.isActive = 1 ORDER BY c.componentCode")
    List<String> findActiveCodes();
}