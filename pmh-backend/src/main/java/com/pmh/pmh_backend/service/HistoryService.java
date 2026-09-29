package com.pmh.pmh_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pmh.pmh_backend.entity.PmhHistory;
import com.pmh.pmh_backend.repository.PmhHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final PmhHistoryRepository repo;

    // Popup "Lịch sử" trong PDF - sort Ngày thực hiện mới -> cũ
    @Transactional(readOnly = true)
    public Page<PmhHistory> search(String tableName, Long recordId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("actionDate").descending());
        return repo.findByTableNameAndRecordId(tableName, recordId, pageable);
    }
}