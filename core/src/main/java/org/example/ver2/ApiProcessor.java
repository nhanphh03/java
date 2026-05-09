package org.example.ver2;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ApiProcessor {

    @Transactional // Đảm bảo tính toàn vẹn dữ liệu khi save DB
    public <P, ID, E, D> D process(P params, ApiIntegrationStrategy<P, ID, E, D> strategy) {

        // B1: Lấy ID và Kiểm tra DB
        ID id = strategy.getId(params);
        E entity = strategy.checkDatabase(id);

        // B2: Nếu không có trong DB thì gọi bên ngoài
        if (entity == null) {
            entity = strategy.callExternal(params);

            // Validate sơ bộ nếu cần
            if (entity == null) {
                throw new RuntimeException("Data not found from external system");
            }

            // Lưu vào DB bằng repository được cung cấp từ strategy
            entity = strategy.getRepository().save(entity);
        }

        // B3: Thực thi các logic phụ (nếu có)
        strategy.postProcess(entity);

        // B4: Map sang DTO và trả về
        return strategy.mapToDto(entity);
    }
}
