package com.pmh.pmh_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pmh.pmh_backend.entity.PmhHistory;

@Repository
public interface PmhHistoryRepository extends JpaRepository<PmhHistory, Long> {
    Page<PmhHistory> findByTableNameAndRecordId(String tableName, Long recordId, Pageable pageable);
}