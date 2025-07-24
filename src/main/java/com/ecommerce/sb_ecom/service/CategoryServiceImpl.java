package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.exceptions.APIException;
import com.ecommerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.sb_ecom.mapper.CategoryMapper;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.payload.CategoryDTO;
import com.ecommerce.sb_ecom.payload.CategoryResponse;
import com.ecommerce.sb_ecom.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements  CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse getAllCategories(){
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()){
            throw new APIException("No categories exist.");
        }

        List<CategoryDTO> categoryDTOS =  categories.stream()
                .map(category -> categoryMapper.categoryToCategoryDTO(category)).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);

        return categoryResponse;
    }

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")  ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();

        if (categories.isEmpty()){
            throw new APIException("No categories exist.");
        }

        List<CategoryDTO> categoryDTOS =  categories.stream()
                .map(category -> categoryMapper.categoryToCategoryDTO(category)).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());


        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO){

        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);
        Category categoryFromDB = categoryRepository.findByCategoryName(category.getCategoryName());

        if (categoryFromDB != null){
            throw new APIException("Category with name " + category.getCategoryName() + " already exists");
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.categoryToCategoryDTO(savedCategory);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        categoryRepository.delete(category);

        return  categoryMapper.categoryToCategoryDTO(category);

    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

        Category foundCategory = categoryRepository.findById(categoryId)
                                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);
        category.setCategoryId(categoryId);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.categoryToCategoryDTO(savedCategory);

    }

}
