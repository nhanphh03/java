package org.example;

import java.util.function.Function;

public class BaseRecordService {

    public <ID, E, D, P> D processInternal(
            P params,
            ID id,
            JpaRepository<E, ID> repository,
            Function<E, D> defaultMapper, // Mapper mặc định (Entity -> DTO)
            // Các hàm tùy chọn (Optional overrides)
            Function<ID, E> manualCheckDb,
            Function<P, E> manualCallExternal,
            Function<E, D> manualMapper
    ) {
        // 1. BƯỚC 1: KIỂM TRA DB
        E entity;
        if (manualCheckDb != null) {
            entity = manualCheckDb.apply(id);
        } else {
            // Dùng mặc định của Repository
            entity = repository.findById(id).orElse(null);
        }

        // 2. BƯỚC 2: GỌI NGOÀI NẾU CHƯA CÓ
        if (entity == null) {
            if (manualCallExternal != null) {
                entity = manualCallExternal.apply(params);
            } else {
                // Logic mặc định nếu không truyền hàm call external
                // (Thường bước này bắt buộc phải có logic vì mỗi API mỗi khác)
                throw new RuntimeException("External call logic is required when record not found");
            }

            // Tự động lưu vào DB sau khi có entity mới (trừ khi bạn muốn handle thủ công bên trong manualCallExternal)
            entity = repository.save(entity);
        }

        // 3. BƯỚC 3: MAPPING
        Function<E, D> finalMapper = (manualMapper != null) ? manualMapper : defaultMapper;
        return finalMapper.apply(entity);
    }
}
