package org.example.ver2;

import org.springframework.stereotype.Component;

@Component
public class ProductIntegrationStrategy implements ApiIntegrationStrategy<ProductReq, String, ProductEntity, ProductDTO> {

    @Autowired private ProductRepository productRepository;
    @Autowired private ExternalProductClient productClient;

    @Override
    public String getId(ProductReq params) {
        return params.getProductCode();
    }

    @Override
    public JpaRepository<ProductEntity, String> getRepository() {
        return productRepository;
    }

    @Override
    public ProductEntity callExternal(ProductReq params) {
        // Logic gọi API riêng của sản phẩm
        var response = productClient.getDetail(params.getProductCode());
        return ProductEntity.builder()
                .code(response.getCode())
                .name(response.getFullName())
                .price(response.getMarketPrice())
                .build();
    }

    @Override
    public ProductDTO mapToDto(ProductEntity entity) {
        return new ProductDTO(entity.getCode(), entity.getName());
    }

    @Override
    public void postProcess(ProductEntity entity) {
        System.out.println("Đã xử lý xong sản phẩm: " + entity.getCode());
    }
}
