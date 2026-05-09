package org.example.ver2;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiIntegrationStrategy<P, ID, E, D> {

    // Lấy ID từ tham số đầu vào
    ID getId(P params);

    // Trả về Repository tương ứng để thao tác với DB
    JpaRepository<E, ID> getRepository();

    // Bước gọi API bên ngoài và tạo Entity mới
    E callExternal(P params);

    // Chuyển đổi Entity sang DTO
    D mapToDto(E entity);

    // Bước kiểm tra DB (có thể ghi đè nếu muốn tìm kiếm phức tạp hơn)
    default E checkDatabase(ID id) {
        return getRepository().findById(id).orElse(null);
    }

    // Bước xử lý bổ sung sau khi có dữ liệu (Hàm móc - Hook)
    default void postProcess(E entity) {
        // Mặc định không làm gì, nhưng các class con có thể ghi đè để log, gửi mail...
    }
}
