package org.example;

public class MyBusinessService extends BaseService {

    @Autowired private UserRepository userRepository;
    @Autowired private UserClient userClient; // Giả sử dùng FeignClient hoặc RestTemplate

    public UserDTO getUser(Long userId, UserRequest request) {
        return processInternal(
                request,
                userId,
                userRepository,
                entity -> modelMapper.map(entity, UserDTO.class), // Default mapper
                null, // Không dùng check DB manual
                req -> {
                    // Chỉ viết logic gọi API ngoài
                    ExternalData data = externalClient.fetch(req);
                    return new UserEntity(data);
                },
                null  // Không dùng mapper manual
        );
    }

    public UserDTO getSpecialUser(Long userId, UserRequest request) {
        return processInternal(
                request,
                userId,
                userRepository,
                entity -> modelMapper.map(entity, UserDTO.class),

                // Manual Check DB: tìm theo Email thay vì ID
                id -> userRepository.findByEmail(request.getEmail()).orElse(null),

                // Manual Call External
                req -> {
                    return externalClient.callSpecial(req);
                },

                // Manual Mapper: thêm logic tính toán trường phức tạp
                entity -> {
                    UserDTO dto = new UserDTO(entity.getName());
                    dto.setNote("Special processed");
                    return dto;
                }
        );
    }

    public UserDTO getUser(Long userId, UserRequest request) {
        return ApiProcessContext.<UserRequest, Long, UserEntity, UserDTO>builder()
                .id(userId)
                .params(request)
                .repository(userRepository)
                .callExternalFunc(req -> externalClient.call(req)) // Chỉ truyền cái cần thiết
                .mapperFunc(entity -> modelMapper.map(entity, UserDTO.class))
                .build()
                .execute();
    }
}
