package com.pmh.pmh_backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pmh.pmh_backend.dto.RejectRequest;
import com.pmh.pmh_backend.dto.groupcategory.GroupCategoryResponse;
import com.pmh.pmh_backend.dto.groupcategory.GroupCategorySaveRequest;
import com.pmh.pmh_backend.dto.groupcategory.GroupCategorySearchRequest;
import com.pmh.pmh_backend.service.ComponentService;
import com.pmh.pmh_backend.service.GroupCategoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/group-categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class GroupCategoryController {

    private final GroupCategoryService service;
    private final ComponentService componentService; // dùng cho dropdown Cấu phần xử lý

    @PostMapping("/search")
    public ResponseEntity<Page<GroupCategoryResponse>> search(@RequestBody GroupCategorySearchRequest req) {
        return ResponseEntity.ok(service.search(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupCategoryResponse> detail(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(service.getDetail(id));
    }

    // MAKER tạo/sửa/gửi/xóa | CHECKER duyệt/từ chối/hủy | VIEWER chỉ xem
    @PreAuthorize("hasAnyRole('ADMIN','MAKER')")
    @PostMapping
    public ResponseEntity<GroupCategoryResponse> create(@Valid @RequestBody GroupCategorySaveRequest req,
            @RequestHeader(value = "X-Username", defaultValue = "system") String username,
            HttpServletRequest http) throws Exception {
        return new ResponseEntity<>(service.create(req, username, http.getRemoteAddr()), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MAKER')")
    @PutMapping("/{id}")
    public ResponseEntity<GroupCategoryResponse> update(@PathVariable Long id,
            @Valid @RequestBody GroupCategorySaveRequest req,
            @RequestHeader(value = "X-Username", defaultValue = "system") String username,
            HttpServletRequest http) throws Exception {
        return ResponseEntity.ok(service.update(id, req, username, http.getRemoteAddr()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MAKER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id,
            @RequestHeader(value = "X-Username", defaultValue = "system") String u, HttpServletRequest h) {
        service.delete(id, u, h.getRemoteAddr()); return ResponseEntity.ok("Xóa thành công");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MAKER')")
    @PostMapping("/send-approve")
    public ResponseEntity<String> send(@RequestBody List<Long> ids,
            @RequestHeader(value = "X-Username", defaultValue = "system") String u, HttpServletRequest h) {
        service.sendApprove(ids, u, h.getRemoteAddr()); return ResponseEntity.ok("Gửi duyệt thành công");
    }

    @PreAuthorize("hasAnyRole('ADMIN','CHECKER')")
    @PostMapping("/approve")
    public ResponseEntity<String> approve(@RequestBody List<Long> ids,
            @RequestHeader(value = "X-Username", defaultValue = "system") String u, HttpServletRequest h) throws Exception {
        service.approve(ids, u, h.getRemoteAddr()); return ResponseEntity.ok("Phê duyệt thành công");
    }

    @PreAuthorize("hasAnyRole('ADMIN','CHECKER')")
    @PostMapping("/reject")
    public ResponseEntity<String> reject(@RequestBody RejectRequest req,
            @RequestHeader(value = "X-Username", defaultValue = "system") String u, HttpServletRequest h) {
        service.reject(req.getIds(), req.getReason(), u, h.getRemoteAddr()); return ResponseEntity.ok("Từ chối thành công");
    }

    @PreAuthorize("hasAnyRole('ADMIN','CHECKER')")
    @PostMapping("/cancel-approve")
    public ResponseEntity<String> cancel(@RequestBody List<Long> ids,
            @RequestHeader(value = "X-Username", defaultValue = "system") String u, HttpServletRequest h) {
        service.cancelApprove(ids, u, h.getRemoteAddr()); return ResponseEntity.ok("Hủy phê duyệt thành công");
    }

    @GetMapping("/component-options")
    public ResponseEntity<List<String>> componentOptions() {
        return ResponseEntity.ok(componentService.getActiveCodes());
    }
}