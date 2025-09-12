package com.mo.moyeo.domain.transaction.history.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.paging.PageResponse;
import com.mo.moyeo.domain.transaction.category.entity.Category;
import com.mo.moyeo.domain.transaction.category.repository.CategoryRepository;
import com.mo.moyeo.domain.transaction.history.dto.TransactionResponse;
import com.mo.moyeo.domain.transaction.history.dto.TransactionSearchCondition;
import com.mo.moyeo.domain.transaction.history.dto.TransactionUpdateRequest;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.transaction.history.repository.BoxHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxHistoryService {

    private final BoxHistoryRepository boxHistoryRepository;
    private final CategoryRepository categoryRepository;

    public PageResponse<TransactionResponse> getTransactions(Long boxId, TransactionSearchCondition request) {
        Slice<BoxHistory> boxHistories = boxHistoryRepository.search(boxId, request);
        return PageResponse.from(boxHistories, TransactionResponse::from);
    }

    @Transactional
    public void updateTransaction(Long historyId, TransactionUpdateRequest request) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "잘못된 카테고리 입니다."));
        }

        BoxHistory boxHistory = boxHistoryRepository.findById(historyId).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        boxHistory.update(request.getMemo(), category);
    }

    public void saveHistory(BoxHistory boxHistory){
        boxHistoryRepository.save(boxHistory);
    }

}
