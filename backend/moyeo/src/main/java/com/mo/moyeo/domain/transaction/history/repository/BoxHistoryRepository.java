package com.mo.moyeo.domain.transaction.history.repository;

import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxHistoryRepository extends JpaRepository<BoxHistory, Long> {
}
