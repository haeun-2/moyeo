package com.mo.moyeo.domain.transaction.category.repository;

import com.mo.moyeo.domain.transaction.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
