package org.example;

import lombok.Builder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.function.Function;

@Builder
public class ApiProcessContext<P, ID, E, D> {
    private P params;
    private ID id;
    private JpaRepository<E, ID> repository;

    // Các logic xử lý
    private Function<ID, E> checkDbFunc;
    private Function<P, E> callExternalFunc;
    private Function<E, D> mapperFunc;

    public D execute() {
        // B1: Check DB (Dùng manual nếu có, không thì dùng repo)
        E entity = (checkDbFunc != null)
                ? checkDbFunc.apply(id)
                : repository.findById(id).orElse(null);

        // B2: Call External nếu không thấy
        if (entity == null) {
            if (callExternalFunc == null) throw new RuntimeException("Missing Call External Logic");
            entity = callExternalFunc.apply(params);
            entity = repository.save(entity); // Auto save
        }

        // B3: Mapping
        if (mapperFunc == null) throw new RuntimeException("Missing Mapper Logic");
        return mapperFunc.apply(entity);
    }
}
