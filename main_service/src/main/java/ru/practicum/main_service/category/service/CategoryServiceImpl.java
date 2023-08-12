package ru.practicum.main_service.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.mapper.CategoryMapper;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.category.repository.CategoryRepository;
import ru.practicum.main_service.common.DBRequest;
import ru.practicum.main_service.common.PageableParser;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final DBRequest<Category> dbRequest;
    private final CategoryRepository repository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository repository) {
        this.dbRequest = new DBRequest<>(repository);
        this.repository = repository;
    }

    @Override
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(NewCategoryDto newCategoryDto) {
        Category category = dbRequest.tryRequest(repository::save, CategoryMapper.makeCategory(newCategoryDto));
        return  CategoryMapper.makeCategoryDto(category);
    }

    @Override
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(Integer catId) {
        dbRequest.checkExistence(Category.class, catId);
        repository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(Integer catId, CategoryDto categoryDto) {
        dbRequest.checkExistence(Category.class, catId);
        Category savedCategory = repository.getReferenceById(catId);
        savedCategory.setName(categoryDto.getName());
        return CategoryMapper.makeCategoryDto(dbRequest.tryRequest(repository::save, savedCategory));
    }

    @Override
    @Transactional
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageableParser.makePageable(from, size);
        return CategoryMapper.makeCategoryDto(repository.findAll(pageable).toList());
    }

    @Override
    @Transactional
    public CategoryDto getCategoryById(Integer catId) {
        dbRequest.checkExistence(Category.class, catId);
        return CategoryMapper.makeCategoryDto(repository.getReferenceById(catId));
    }
}
