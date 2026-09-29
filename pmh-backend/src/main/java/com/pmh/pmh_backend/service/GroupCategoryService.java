package com.pmh.pmh_backend.service;

import com.pmh.pmh_backend.dto.groupcategory.*;
import com.pmh.pmh_backend.entity.*;
import com.pmh.pmh_backend.repository.*;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Tự inject Repository
@Transactional
public class GroupCategoryService {

    private final GroupCategoryRepository repo; // Người giao tiếp DB
    private final PmhHistoryRepository historyRepo; // Lưu lịch sử thao tác
    private final ObjectMapper mapper = new ObjectMapper(); // Convert object <-> JSON

    // 1. TÌM KIẾM: PDF 1.1 Danh mục - 3 Text LIKE + 2 Select IN + sort Ngày cập nhật mới->cũ
    @Transactional(readOnly = true)
    public Page<GroupCategoryResponse> search(GroupCategorySearchRequest req) {
        Specification<PmhGroupCategory> spec = Specification.where(GroupCategorySpecification.paramTypeLike(req.getParamType()))
                .and(GroupCategorySpecification.paramValueLike(req.getParamValue())) // Tìm mờ LIKE %...%
                .and(GroupCategorySpecification.paramNameLike(req.getParamName())) // Bỏ qua nếu rỗng (return null)
                .and(GroupCategorySpecification.hasStatuses(req.getStatuses())) // Lọc IN 1,3,4,5,7
                .and(GroupCategorySpecification.hasIsActives(req.getIsActives())); // Lọc IN 0,1

        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), Sort.by("updatedDate").descending()); // Mặc định PDF
        return repo.findAll(spec, pageable).map(this::toResponse); // Chuyển Entity -> DTO
    }

    // 2. XEM CHI TIẾT: trả Dữ liệu cũ | Dữ liệu mới để FE bôi đỏ
    @Transactional(readOnly = true)
    public GroupCategoryResponse getDetail(Long id) throws Exception {
        PmhGroupCategory e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi"));
        GroupCategoryResponse res = toResponse(e); // Dữ liệu cũ
        if (e.getNewData() != null && !e.getNewData().isBlank()) {
            res.setNewData(mapper.readValue(e.getNewData(), Map.class)); // Parse JSON NEW_DATA thành Dữ liệu mới
            res.setChangedFields(compare(e)); // List field thay đổi để bôi đỏ
        }
        return res;
    }

    // 3. THÊM MỚI / SAO CHÉP: status=1-Mới, isDisplay=1-Cho xóa
    public GroupCategoryResponse create(GroupCategorySaveRequest req, String username, String ip) throws Exception {
        if (repo.existsByParamTypeAndParamValue(req.getParamType(), req.getParamValue())) // Check trùng cặp Type+Value (PDF 2.2: Y)
            throw new RuntimeException("Đã tồn tại cặp Danh mục [" + req.getParamType() + "] + Giá trị [" + req.getParamValue() + "]");
        validateDate(req.getEffectiveDate(), req.getEndEffectiveDate()); // Cấm ngày quá khứ

        PmhGroupCategory e = new PmhGroupCategory();
        copyReqToEntity(req, e); // Copy form -> Entity
        e.setStatus(1); e.setIsDisplay(1); // Mới + Cho phép xóa
        if (e.getIsActive() == null) e.setIsActive(1); // Mặc định Hoạt động
        repo.save(e);
        saveHistory(e.getId(), "PMH_GROUP_CATEGORY", "Thêm mới", username, ip, "Thêm mới " + req.getParamType() + "-" + req.getParamValue());
        return toResponse(e);
    }

    // 4. SỬA theo note anh Tuấn Anh: 1->sửa thẳng, 5/7->lưu NEW_DATA, 3/4->cấm sửa
    public GroupCategoryResponse update(Long id, GroupCategorySaveRequest req, String username, String ip) throws Exception {
        PmhGroupCategory e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy"));
        if (e.getStatus() == 3) throw new RuntimeException("Bản ghi đang Chờ duyệt, không được sửa");
        if (e.getStatus() == 4) throw new RuntimeException("Bản ghi Đã duyệt, vui lòng Hủy duyệt trước khi sửa");
        if (repo.existsByParamTypeAndParamValueAndIdNot(req.getParamType(), req.getParamValue(), id))
            throw new RuntimeException("Trùng Danh mục + Giá trị với bản ghi khác");
        validateDate(req.getEffectiveDate(), req.getEndEffectiveDate());

        if (e.getStatus() == 1) {
            copyReqToEntity(req, e); // Mới -> update trực tiếp
        } else { // 5-Từ chối / 7-Hủy duyệt -> chỉ lưu nháp
            e.setNewData(mapper.writeValueAsString(req));
        }
        repo.save(e);
        saveHistory(id, "PMH_GROUP_CATEGORY", "Sửa", username, ip, "Sửa " + e.getParamType() + "-" + e.getParamValue());
        return getDetail(id);
    }

    // 5. XÓA: chỉ khi status=1-Mới (đúng bảng nút anh Tuấn Anh, bỏ check isDisplay)
    public void delete(Long id, String username, String ip) {
        PmhGroupCategory e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy"));
        if (e.getStatus() != 1)
            throw new RuntimeException("Chỉ được xóa bản ghi ở trạng thái Mới");
        repo.deleteById(id);
        saveHistory(id, "PMH_GROUP_CATEGORY", "Xóa bản ghi", username, ip, "Xóa " + e.getParamType() + "-" + e.getParamValue());
    }

    // 6. GỬI DUYỆT: 1-Mới / 5-Từ chối / 7-Hủy duyệt -> 3-Chờ duyệt (1 hoặc nhiều bản ghi)
    public void sendApprove(List<Long> ids, String username, String ip) {
        for (Long id : ids) {
            PmhGroupCategory e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy " + id));
            if (e.getStatus() == 3 || e.getStatus() == 4) throw new RuntimeException("Chỉ gửi duyệt từ Mới/Từ chối/Hủy duyệt");
            e.setStatus(3); repo.save(e);
            saveHistory(id, "PMH_GROUP_CATEGORY", "Gửi duyệt", username, ip, "Gửi duyệt " + e.getParamType() + "-" + e.getParamValue());
        }
    }

    // 7. DUYỆT (1 hoặc nhiều): 3 -> 4, apply NEW_DATA nếu có, isDisplay=2
    public void approve(List<Long> ids, String username, String ip) throws Exception {
        for (Long id : ids) {
            PmhGroupCategory e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy " + id));
            if (e.getStatus() != 3) throw new RuntimeException("Chỉ duyệt bản Chờ duyệt");
            if (e.getNewData() != null && !e.getNewData().isBlank()) {
                GroupCategorySaveRequest draft = mapper.readValue(e.getNewData(), GroupCategorySaveRequest.class); // Lấy nháp ra
                copyReqToEntity(draft, e); // Ghi đè vào dữ liệu chính
                e.setNewData(null); // Xóa nháp
            }
            e.setStatus(4); e.setIsDisplay(2); // Đã duyệt + Khóa xóa
            repo.save(e);
            saveHistory(id, "PMH_GROUP_CATEGORY", "Phê duyệt", username, ip, "Phê duyệt " + e.getParamType() + "-" + e.getParamValue());
        }
    }

    // 8. TỪ CHỐI: chỉ khi 3-Chờ duyệt -> 5, bắt nhập lý do
    public void reject(List<Long> ids, String reason, String username, String ip) {
        if (reason == null || reason.isBlank()) throw new RuntimeException("Phải nhập Lý do từ chối");
        for (Long id : ids) {
            PmhGroupCategory e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy " + id));
            if (e.getStatus() != 3) throw new RuntimeException("Chỉ được Từ chối bản ghi Chờ duyệt");
            e.setStatus(5); repo.save(e);
            saveHistory(id, "PMH_GROUP_CATEGORY", "Từ chối", username, ip, "Từ chối " + e.getParamType() + ": " + reason);
        }
    }

    // 9. HỦY DUYỆT: 4-Đã duyệt -> 7-Hủy duyệt
    public void cancelApprove(List<Long> ids, String username, String ip) {
        for (Long id : ids) {
            PmhGroupCategory e = repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy " + id));
            if (e.getStatus() != 4) throw new RuntimeException("Chỉ hủy duyệt bản Đã duyệt");
            e.setStatus(7); repo.save(e);
            saveHistory(id, "PMH_GROUP_CATEGORY", "Hủy phê duyệt", username, ip, "Hủy phê duyệt " + e.getParamType());
        }
    }

    // --- Hàm phụ dùng chung ---
    private void copyReqToEntity(GroupCategorySaveRequest req, PmhGroupCategory e) { // Copy form -> Entity
        e.setParamName(req.getParamName()); // Tên thành phần
        e.setParamValue(req.getParamValue()); // Giá trị TP
        e.setParamType(req.getParamType()); // Danh mục nhóm
        e.setDescription(req.getDescription()); // Mô tả
        e.setComponentCode(req.getComponentCode() == null ? null : String.join(",", req.getComponentCode())); // List RLT,FIM,TRA -> chuỗi "RLT,FIM,TRA"
        e.setEffectiveDate(req.getEffectiveDate()); // Ngày HL
        e.setEndEffectiveDate(req.getEndEffectiveDate()); // Ngày hết HL
    }

    private GroupCategoryResponse toResponse(PmhGroupCategory e) { // Entity -> DTO trả về Grid
        GroupCategoryResponse r = new GroupCategoryResponse();
        r.setId(e.getId()); r.setParamName(e.getParamName()); r.setParamValue(e.getParamValue());
        r.setParamType(e.getParamType()); r.setDescription(e.getDescription());
        r.setComponentCode(e.getComponentCode()); r.setStatus(e.getStatus()); r.setIsActive(e.getIsActive());
        r.setEffectiveDate(e.getEffectiveDate()); r.setEndEffectiveDate(e.getEndEffectiveDate());
        r.setOldData(e); // Dữ liệu cũ
        return r;
    }

    private void validateDate(LocalDateTime from, LocalDateTime to) { // Validate ngày theo PDF 2.2
        if (from != null && from.isBefore(LocalDateTime.now().minusDays(1)))
            throw new RuntimeException("Ngày hiệu lực không hợp lệ (quá khứ), cần bôi đỏ"); // NSD nhập quá khứ -> cảnh báo
        if (from != null && to != null && to.isBefore(from))
            throw new RuntimeException("Ngày hết HL phải sau Ngày HL");
    }

    private void saveHistory(Long recordId, String table, String action, String user, String ip, String content) { // Ghi lịch sử
        PmhHistory h = new PmhHistory();
        h.setRecordId(recordId); h.setTableName(table); h.setActionType(action);
        h.setUsername(user); h.setIpAddress(ip); h.setActionDate(LocalDateTime.now()); h.setContent(content);
        historyRepo.save(h);
    }

    private List<String> compare(PmhGroupCategory e) { return new ArrayList<>(); } // TODO: so sánh old vs new để bôi đỏ, làm sau
}