package com.pmh.pmh_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pmh.pmh_backend.entity.PmhRole;

@Repository
public interface PmhRoleRepository extends JpaRepository<PmhRole, Long> {
    Optional<PmhRole> findByRoleCode(String roleCode); // Tìm role để gán cho user
}
