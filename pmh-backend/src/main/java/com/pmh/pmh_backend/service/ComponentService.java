package com.pmh.pmh_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pmh.pmh_backend.dto.component.ComponentResponse;
import com.pmh.pmh_backend.dto.component.ComponentSaveRequest;
import com.pmh.pmh_backend.dto.component.ComponentSearchRequest;
import com.pmh.pmh_backend.entity.PmhComponent;
import com.pmh.pmh_backend.entity.PmhHistory;
import com.pmh.pmh_backend.repository.ComponentRepository;
import com.pmh.pmh_backend.repository.ComponentSpecification;
import com.pmh.pmh_backend.repository.PmhHistoryRepository;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Transactional // atomic cho các thao tác duyệt/từ chối nhiều bản ghi
public class ComponentService {

    private final ComponentRepository repo;
    private final PmhHistoryRepository historyRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public Page<ComponentResponse> search(ComponentSearchRequest req) {
        Specification<PmhComponent> spec = Specification.where(ComponentSpecification.hasComponentCodes(req.getComponentCodes()))
                .and(ComponentSpecification.hasComponentNames(req.getComponentNames()))
                .and(ComponentSpecification.hasStatuses(req.getStatuses()))
                .and(ComponentSpecification.hasIsActives(req.getIsActives()));

        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), Sort.by("updatedDate").descending());
        return repo.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ComponentResponse getDetail(Long id) throws Exception {
        PmhComponent e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi"));
        ComponentResponse res = toResponse(e);
        if (e.getNewData() != null && !e.getNewData().isBlank()) {
            res.setNewData(mapper.readValue(e.getNewData(), Map.class));
            res.setChangedFields(compare(e));
        }
        return res;
    }

    public ComponentResponse create(ComponentSaveRequest req, String username, String ip) throws Exception {
        if (repo.existsByComponentCode(req.getComponentCode()))
            throw new RuntimeException("Mã cấu phần đã tồn tại: " + req.getComponentCode());
        validateDate(req.getEffectiveDate(), req.getEndEffectiveDate());

        PmhComponent e = new PmhComponent();
        copyReqToEntity(req, e);
        e.setStatus(1); e.setIsDisplay(1);
        if (e.getIsActive() == null) e.setIsActive(1);
        repo.save(e);
        saveHistory(e.getId(), "PMH_COMPONENTS", "Thêm mới", username, ip, "Thêm mới " + e.getComponentCode());
        return toResponse(e);
    }

    public ComponentResponse update(Long id, ComponentSaveRequest req, String username, String ip) throws Exception {
    PmhComponent e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy"));
    if (!e.getComponentCode().equals(req.getComponentCode()))
      throw new RuntimeException("Không được đổi Mã cấu phần khi sửa");
    if (e.getStatus() == 3) throw new RuntimeException("Bản ghi đang Chờ duyệt, không được sửa");
    if (e.getStatus() == 4) throw new RuntimeException("Bản ghi Đã duyệt, vui lòng Hủy duyệt trước khi sửa");
    validateDate(req.getEffectiveDate(), req.getEndEffectiveDate());

    if (e.getStatus() == 1) {
        copyReqToEntity(req, e);                          // Mới -> update dữ liệu trực tiếp
    } else {                                               // status == 5 hoặc 7
        e.setNewData(mapper.writeValueAsString(req));      // Từ chối / Hủy duyệt -> chỉ lưu nháp
    }
    repo.save(e);
    saveHistory(id, "PMH_COMPONENTS", "Sửa", username, ip, "Sửa " + e.getComponentCode());
    return getDetail(id);
    }

    public void delete(Long id, String username, String ip) {
    PmhComponent e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy"));
    if (e.getStatus() != 1) throw new RuntimeException("Chỉ được xóa bản ghi ở trạng thái Mới");
    repo.deleteById(id);
    saveHistory(id, "PMH_COMPONENTS", "Xóa bản ghi", username, ip, "Xóa " + e.getComponentCode());
    }

    public void sendApprove(List<Long> ids, String username, String ip) {
        for (Long id : ids) {
            PmhComponent e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy " + id));
            if (e.getStatus() == 3 || e.getStatus() == 4) throw new RuntimeException("Chỉ gửi duyệt từ Mới/Từ chối/Hủy duyệt");
            e.setStatus(3); repo.save(e);
            saveHistory(id, "PMH_COMPONENTS", "Gửi duyệt", username, ip, "Gửi duyệt " + e.getComponentCode());
        }
    }

    public void approve(List<Long> ids, String username, String ip) throws Exception {
        for (Long id : ids) {
            PmhComponent e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy " + id));
            if (e.getStatus() != 3) throw new RuntimeException("Chỉ duyệt bản Chờ duyệt");
            if (e.getNewData() != null && !e.getNewData().isBlank()) {
                ComponentSaveRequest draft = mapper.readValue(e.getNewData(), ComponentSaveRequest.class);
                copyReqToEntity(draft, e);
                e.setNewData(null);
            }
            e.setStatus(4); e.setIsDisplay(2);
            repo.save(e);
            saveHistory(id, "PMH_COMPONENTS", "Phê duyệt", username, ip, "Phê duyệt " + e.getComponentCode());
        }
    }

    public void reject(List<Long> ids, String reason, String username, String ip) {
    if (reason == null || reason.isBlank()) throw new RuntimeException("Phải nhập Lý do từ chối");
    for (Long id : ids) {
        PmhComponent e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy " + id));
        if (e.getStatus() != 3) throw new RuntimeException("Chỉ được Từ chối bản ghi Chờ duyệt");
        e.setStatus(5); repo.save(e);
        saveHistory(id, "PMH_COMPONENTS", "Từ chối", username, ip, "Từ chối " + e.getComponentCode() + ": " + reason);
        }
    }

    public void cancelApprove(List<Long> ids, String username, String ip) {
        for (Long id : ids) {
            PmhComponent e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy " + id));
            if (e.getStatus() != 4) throw new RuntimeException("Chỉ hủy duyệt bản Đã duyệt");
            e.setStatus(7); repo.save(e);
            saveHistory(id, "PMH_COMPONENTS", "Hủy phê duyệt", username, ip, "Hủy phê duyệt " + e.getComponentCode());
        }
    }

    private void copyReqToEntity(ComponentSaveRequest req, PmhComponent e) {
        e.setComponentCode(req.getComponentCode());
        e.setComponentName(req.getComponentName());
        e.setMessageType(req.getMessageType() == null ? null : String.join(",", req.getMessageType()));
        e.setConnectionMethod(req.getConnectionMethod() == null ? null : String.join(",", req.getConnectionMethod()));
        e.setCheckToken(req.getCheckToken() == null ? "N" : req.getCheckToken());
        e.setEffectiveDate(req.getEffectiveDate());
        e.setEndEffectiveDate(req.getEndEffectiveDate());
    }

    private ComponentResponse toResponse(PmhComponent e) {
        ComponentResponse r = new ComponentResponse();
        r.setId(e.getId()); r.setComponentCode(e.getComponentCode()); r.setComponentName(e.getComponentName());
        r.setMessageType(e.getMessageType()); r.setConnectionMethod(e.getConnectionMethod());
        r.setCheckToken(e.getCheckToken()); r.setStatus(e.getStatus()); r.setIsActive(e.getIsActive());
        r.setEffectiveDate(e.getEffectiveDate()); r.setEndEffectiveDate(e.getEndEffectiveDate());
        r.setOldData(e);
        return r;
    }

    private void validateDate(LocalDateTime from, LocalDateTime to) {
        if (from != null && from.isBefore(LocalDateTime.now().minusDays(1)))
            throw new RuntimeException("Ngày hiệu lực không hợp lệ (quá khứ), cần bôi đỏ");
        if (from != null && to != null && to.isBefore(from))
            throw new RuntimeException("Ngày hết HL phải sau Ngày HL");
    }

    private void saveHistory(Long recordId, String table, String action, String user, String ip, String content) {
        PmhHistory h = new PmhHistory();
        h.setRecordId(recordId); h.setTableName(table); h.setActionType(action);
        h.setUsername(user); h.setIpAddress(ip); h.setActionDate(LocalDateTime.now()); h.setContent(content);
        historyRepo.save(h);
    }

    private List<String> compare(PmhComponent e) { return new ArrayList<>(); }

    @Transactional(readOnly = true)
    public List<String> getDistinctCodes() { return repo.findDistinctCodes(); }

    @Transactional(readOnly = true)
    public List<String> getDistinctNames() { return repo.findDistinctNames(); }

    // Dropdown "Cấu phần xử lý" cho màn Group Category (chỉ lấy IS_ACTIVE=1)
    @Transactional(readOnly = true)
    public List<String> getActiveCodes() { return repo.findActiveCodes(); }
}