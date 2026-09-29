package com.pmh.pmh_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pmh.pmh_backend.entity.PmhUser;

public interface AppUserRepository extends JpaRepository<PmhUser, Long>{
    Optional<PmhUser> findByUsername(String u);
}
