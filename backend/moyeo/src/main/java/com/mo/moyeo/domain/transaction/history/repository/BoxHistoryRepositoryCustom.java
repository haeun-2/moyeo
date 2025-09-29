package com.mo.moyeo.domain.transaction.history.repository;

import com.mo.moyeo.domain.transaction.history.dto.TransactionSearchCondition;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import org.springframework.data.domain.Slice;

public interface BoxHistoryRepositoryCustom {

    Slice<BoxHistory> search(Long boxId, TransactionSearchCondition condition);

}
