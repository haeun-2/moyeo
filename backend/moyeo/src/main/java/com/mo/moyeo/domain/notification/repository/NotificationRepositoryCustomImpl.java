package com.mo.moyeo.domain.notification.repository;

import com.mo.moyeo.domain.notification.dto.NotificationSearchCondition;
import com.mo.moyeo.domain.notification.entity.Notification;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import static com.mo.moyeo.domain.notification.entity.QNotification.notification;

import java.util.List;

@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Notification> search(Long userId, NotificationSearchCondition condition) {

        // 페이징
        int page = condition.getPage() != null ? condition.getPage() : 0;
        int size = condition.getSize() != null ? condition.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);

        // 쿼리 실행
        List<Notification> result = jpaQueryFactory
                .selectFrom(notification)
                .where(notification.userId.eq(userId))
                .orderBy(order(condition.getSortDir()), notification.id.desc())
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

    private static OrderSpecifier<?> order(NotificationSearchCondition.SortDirection sortDirection) {
        return sortDirection == NotificationSearchCondition.SortDirection.ASC
                ? notification.receivedAt.asc()
                : notification.receivedAt.desc();
    }
}
