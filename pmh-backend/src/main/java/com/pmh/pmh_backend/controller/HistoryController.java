package com.pmh.pmh_backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pmh.pmh_backend.entity.PmhHistory;
import com.pmh.pmh_backend.service.HistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/histories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class HistoryController {

    private final HistoryService service;

    // GET /api/histories?tableName=PMH_COMPONENTS&recordId=1
    // tableName dùng đúng giá trị BE đang ghi: "PMH_GROUP_CATEGORY" hoặc "PMH_COMPONENTS"
    @GetMapping
    public ResponseEntity<Page<PmhHistory>> search(
            @RequestParam String tableName,
            @RequestParam Long recordId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.search(tableName, recordId, page, size));
    }
}