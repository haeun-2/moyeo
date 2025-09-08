package com.mo.moyeo.common.paging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private Integer page;
    private Integer size;
    private Boolean hasNext;
    private List<T> content;

    // 페이징 변환 유틸 메서드
    public static <T> PageResponse<T> from(Slice<T> data) {
        return new PageResponse<>(
                data.getNumber(),
                data.getSize(),
                data.hasNext(),
                data.getContent()
        );
    }

    public static <T, R> PageResponse<R> from(Slice<T> data, Function<? super T, ? extends R> mapper) {
        List<R> mappedContent = data.getContent()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());

        return new PageResponse<>(
                data.getNumber(),
                data.getSize(),
                data.hasNext(),
                mappedContent
        );
    }

}
