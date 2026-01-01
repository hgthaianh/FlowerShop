package vn.quahoa.flowershop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quahoa.flowershop.dto.category.CategoryRequest;
import vn.quahoa.flowershop.exception.ResourceNotFoundException;
import vn.quahoa.flowershop.exception.ValidationException;
import vn.quahoa.flowershop.model.Category;
import vn.quahoa.flowershop.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(CategoryRequest request) {
        validateUniqueName(request.getName(), null);
        Category category = new Category();
        category.setName(request.getName());
        return categoryRepository.save(category);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = getById(id);
        validateUniqueName(request.getName(), id);
        category.setName(request.getName());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = getById(id);
        categoryRepository.delete(category);
    }

    private void validateUniqueName(String name, Long currentId) {
        categoryRepository.findByNameIgnoreCase(name).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new ValidationException("name", "Category name already exists");
            }
        });
    }
}
