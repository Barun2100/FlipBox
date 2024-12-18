package com.ecom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.model.Category;

@Service
public interface CategoryService {

	public Category saveCategory(Category category);

	public boolean existCategory(String name);

	public List<Category> getAllCategory();

	public boolean deleteCategory(int id);

	public Category getCategoryById(int id);
}
