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
import com.pmh.pmh_backend.dto.component.ComponentResponse;
import com.pmh.pmh_backend.dto.component.ComponentSaveRequest;
import com.pmh.pmh_backend.dto.component.ComponentSearchRequest;
import com.pmh.pmh_backend.service.ComponentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/components")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ComponentController {

    private final ComponentService service;

    @PostMapping("/search")
    public ResponseEntity<Page<ComponentResponse>> search(@RequestBody ComponentSearchRequest req) {
        return ResponseEntity.ok(service.search(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComponentResponse> detail(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(service.getDetail(id));
    }

    // MAKER tạo/sửa/gửi/xóa | CHECKER duyệt/từ chối/hủy | VIEWER chỉ xem (search/detail)
    @PreAuthorize("hasAnyRole('ADMIN','MAKER')")
    @PostMapping
    public ResponseEntity<ComponentResponse> create(
            @Valid @RequestBody ComponentSaveRequest req,
            @RequestHeader(value = "X-Username", defaultValue = "system") String username,
            HttpServletRequest http) throws Exception {
        return new ResponseEntity<>(service.create(req, username, http.getRemoteAddr()), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MAKER')")
    @PutMapping("/{id}")
    public ResponseEntity<ComponentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ComponentSaveRequest req,
            @RequestHeader(value = "X-Username", defaultValue = "system") String username,
            HttpServletRequest http) throws Exception {
        return ResponseEntity.ok(service.update(id, req, username, http.getRemoteAddr()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MAKER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id,
            @RequestHeader(value = "X-Username", defaultValue = "system") String username,
            HttpServletRequest http) {
        service.delete(id, username, http.getRemoteAddr());
        return ResponseEntity.ok("Xóa thành công");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MAKER')")
    @PostMapping("/send-approve")
    public ResponseEntity<String> sendApprove(@RequestBody List<Long> ids,
            @RequestHeader(value = "X-Username", defaultValue = "system") String username,
            HttpServletRequest http) {
        service.sendApprove(ids, username, http.getRemoteAddr());
        return ResponseEntity.ok("Gửi duyệt thành công");
    }

    @PreAuthorize("hasAnyRole('ADMIN','CHECKER')")
    @PostMapping("/approve")
    public ResponseEntity<String> approve(@RequestBody List<Long> ids,
            @RequestHeader(value = "X-Username", defaultValue = "system") String username,
            HttpServletRequest http) throws Exception {
        service.approve(ids, username, http.getRemoteAddr());
        return ResponseEntity.ok("Phê duyệt thành công");
    }

    @PreAuthorize("hasAnyRole('ADMIN','CHECKER')")
    @PostMapping("/reject")
    public ResponseEntity<String> reject(@RequestBody RejectRequest req,
            @RequestHeader(value = "X-Username", defaultValue = "system") String username,
            HttpServletRequest http) {
        service.reject(req.getIds(), req.getReason(), username, http.getRemoteAddr());
        return ResponseEntity.ok("Từ chối thành công");
    }

    @PreAuthorize("hasAnyRole('ADMIN','CHECKER')")
    @PostMapping("/cancel-approve")
    public ResponseEntity<String> cancel(@RequestBody List<Long> ids,
            @RequestHeader(value = "X-Username", defaultValue = "system") String username,
            HttpServletRequest http) {
        service.cancelApprove(ids, username, http.getRemoteAddr());
        return ResponseEntity.ok("Hủy phê duyệt thành công");
    }

    @GetMapping("/dropdown/codes")
    public ResponseEntity<List<String>> getCodes() {
        return ResponseEntity.ok(service.getDistinctCodes());
    }

    @GetMapping("/dropdown/names")
    public ResponseEntity<List<String>> getNames() {
        return ResponseEntity.ok(service.getDistinctNames());
    }
}