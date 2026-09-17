package com.deveyk.jobmatch.shared.presentation.response;

import com.deveyk.jobmatch.shared.domain.model.JmPage;
import com.deveyk.jobmatch.shared.domain.model.JmSort;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class JmPageResponse<R> {

    private List<R> content;

    private Integer pageNumber;

    private Integer pageSize;

    private Integer totalPageCount;

    private Long totalElementCount;

    private List<JmSort.JmOrder> orderedBy;

    private Object filteredBy;

    public static class JmPageResponseBuilder<R> {

        public <M> JmPageResponse.JmPageResponseBuilder<R> of(final JmPage<M> page, final List<R> content) {
            return JmPageResponse.<R>builder()
                    .content(content)
                    .pageNumber(page.getPageNumber())
                    .pageSize(page.getPageSize())
                    .totalPageCount(page.getTotalPageCount())
                    .totalElementCount(page.getTotalElementCount())
                    .orderedBy(page.getOrderedBy())
                    .filteredBy(page.getFilteredBy());
        }
    }

}
