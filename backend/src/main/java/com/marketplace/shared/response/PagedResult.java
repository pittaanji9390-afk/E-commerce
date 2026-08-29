package com.marketplace.shared.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResult<T> {

    @Builder.Default
    private boolean success = true;

    private List<T> data;

    private PaginationMeta pagination;

    private String message;

    @Builder.Default
    private Instant timestamp = Instant.now();

    public static <T> PagedResult<T> of(Page<T> page) {
        return PagedResult.<T>builder()
                .success(true)
                .data(page.getContent())
                .pagination(PaginationMeta.fromPage(page))
                .message("Success")
                .timestamp(Instant.now())
                .build();
    }

    public static <T> PagedResult<T> of(List<T> content, Page<?> pageMeta) {
        return PagedResult.<T>builder()
                .success(true)
                .data(content)
                .pagination(PaginationMeta.fromPage(pageMeta))
                .message("Success")
                .timestamp(Instant.now())
                .build();
    }
}
