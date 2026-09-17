package com.deveyk.jobmatch.shared.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@Builder
public class JmPage<R> {

    private List<R> content;

    private Integer pageNumber;

    private Integer pageSize;

    private Integer totalPageCount;

    private Long totalElementCount;

    private List<JmSort.JmOrder> orderedBy;

    private Object filteredBy;

    public static <E, C> JmPage<C> of(final Page<E> pageableEntities,
                                       final List<C> content) {

        return JmPage.of(null, pageableEntities, content);
    }


    public static <E, C> JmPage<C> of(final Object filter,
                                       final Page<E> pageableEntities,
                                       final List<C> content) {

        final var responseBuilder = JmPage.<C>builder()
                .content(content)
                .pageNumber(pageableEntities.getNumber() + 1)
                .pageSize(content.size())
                .totalPageCount(pageableEntities.getTotalPages())
                .totalElementCount(pageableEntities.getTotalElements())
                .filteredBy(filter);

        if (pageableEntities.getSort().isSorted()) {
            responseBuilder.orderedBy(JmSort.of(pageableEntities.getSort()).getOrders());
        }

        return responseBuilder.build();
    }

}
