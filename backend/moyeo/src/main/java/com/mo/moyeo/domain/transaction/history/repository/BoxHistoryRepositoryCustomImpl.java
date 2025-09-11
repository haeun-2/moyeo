package com.mo.moyeo.domain.transaction.history.repository;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.transaction.history.dto.TransactionSearchCondition;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.mo.moyeo.domain.transaction.history.entity.QBoxHistory.boxHistory;

@RequiredArgsConstructor
public class BoxHistoryRepositoryCustomImpl implements BoxHistoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<BoxHistory> search(Long boxId, TransactionSearchCondition condition) {

        // 페이징
        int page = condition.getPage() != null ? condition.getPage() : 0;
        int size = condition.getSize() != null ? condition.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);

        // 쿼리 실행
        List<BoxHistory> result = jpaQueryFactory
                .selectFrom(boxHistory)
                .leftJoin(boxHistory.transaction)
                .where(whereCondition(boxId, condition))
                .orderBy(order(condition), boxHistory.id.desc())
                .offset(pageable.getOffset())
                .limit(size + 1)
                .fetch();

        // Slice 처리
        boolean hasNext = result.size() > size;
        if (hasNext) {
            result.remove(size);
        }

        return new SliceImpl<>(result, pageable, hasNext);
    }

    private BooleanBuilder whereCondition(Long boxId, TransactionSearchCondition condition) {
        return new BooleanBuilder()
                .and(boxIdEq(boxId))
                .and(dateBetween(condition.getStartDate(), condition.getEndDate()))
                .and(keywordContains(condition.getKeyword()))
                .and(typeEq(condition.getType()))
                .and(categoryEq(condition.getCategoryId()))
                .and(currencyEq(condition.getCurrency()));
    }

    private BooleanExpression boxIdEq(Long boxId) {
        return boxHistory.box.id.eq(boxId);
    }

    private BooleanExpression dateBetween(LocalDate start, LocalDate end) {
        if (start != null && end != null) {
            return boxHistory.createdAt.between(
                    start.atStartOfDay(),
                    end.atTime(LocalTime.MAX)
            );
        } else if (start != null) {
            return boxHistory.createdAt.goe(start.atStartOfDay());
        } else if (end != null) {
            return boxHistory.createdAt.loe(end.atTime(LocalTime.MAX));
        }
        return null;
    }

    private BooleanExpression keywordContains(String keyword) {
        return (keyword != null && !keyword.isBlank()) ? boxHistory.title.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression typeEq(TransactionSearchCondition.Type type) {
        if (type == null) return null;
        return switch(type) {
            case DEPOSIT -> boxHistory.amount.gt(0);
            case WITHDRAW -> boxHistory.amount.lt(0);
        };
    }

    private BooleanExpression categoryEq(Long categoryId) {
        return categoryId != null ? boxHistory.transaction.category.id.eq(categoryId) : null;
    }

    private BooleanExpression currencyEq(CurrencyType currency) {
        return currency != null ? boxHistory.currencyCode.eq(currency) : null;
    }

    private static OrderSpecifier<?> order(TransactionSearchCondition condition) {
        return condition.getSortDir() == TransactionSearchCondition.SortDirection.ASC
                ? boxHistory.createdAt.asc()
                : boxHistory.createdAt.desc();
    }

}
