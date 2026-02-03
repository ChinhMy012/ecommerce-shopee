package com.example.ecommerce_backend.service.impl;

import com.example.ecommerce_backend.dto.response.category.CategoryTreeResponse;
import com.example.ecommerce_backend.entity.Category;
import com.example.ecommerce_backend.repository.CategoryRepository;
import com.example.ecommerce_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    // Inject CategoryRepository
    // Dùng để truy vấn dữ liệu category từ database
    private final CategoryRepository categoryRepository;

    /**
     * Lấy cây danh mục (category tree)
     * - Chỉ lấy category GỐC (parent = null)
     * - status = 1 (đang hoạt động)
     * - Sau đó build cây bằng đệ quy
     */
    @Override
    public List<CategoryTreeResponse> getCategoryTree() {

        // 1. Lấy tất cả category gốc (không có cha)
        // Ví dụ: "Điện tử", "Thời trang"
        List<Category> rootCategories =
                categoryRepository.findByParentIsNullAndStatus(
                        Category.CategoryStatus.ACTIVE
                );

        // 2. Chuyển từ Entity → DTO
        // 3. Với mỗi category gốc, build ra cây con của nó
        return rootCategories.stream()
                .map(this::mapToTree) // gọi hàm đệ quy
                .toList();
    }

    /**
     * Hàm đệ quy để convert:
     * Category (Entity)
     * → CategoryTreeResponse (DTO)
     *
     * @param category category hiện tại
     * @return DTO chứa category + children
     */
    private CategoryTreeResponse mapToTree(Category category) {

        // 1. Tạo DTO mới
        CategoryTreeResponse dto = new CategoryTreeResponse();

        // 2. Map các field cơ bản
        dto.setId(category.getId());
        dto.setName(category.getName());

        // 3. Xử lý danh sách category con
        // Nếu category này có children
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {

            // 4. Lọc ra các category con còn hoạt động (status = 1)
            // 5. Với mỗi category con → gọi lại mapToTree (đệ quy)
            dto.setChildren(
                    category.getChildren().stream()
                            .filter(child -> child.getStatus() == Category.CategoryStatus.ACTIVE)
                            .map(this::mapToTree)
                            .toList()
            );

        } else {
            // 6. Nếu không có children → trả về danh sách rỗng
            dto.setChildren(List.of());
        }

        // 7. Trả về DTO đã build xong
        return dto;
    }
}
