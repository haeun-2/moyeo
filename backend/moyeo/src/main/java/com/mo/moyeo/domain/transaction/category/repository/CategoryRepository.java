package com.mo.moyeo.domain.transaction.category.repository;

import com.mo.moyeo.domain.transaction.category.entity.Category;
import com.mo.moyeo.domain.transaction.category.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(CategoryType name);

    List<Category> findAllByOrderByIdAsc();

}
