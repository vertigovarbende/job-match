package com.deveyk.jobmatch.shared.presentation.rest.request;

import com.deveyk.jobmatch.shared.domain.model.JmSort;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class JmPageable extends JmSort {

    private int page;

    @Range(min = 1, max = 100, message = "must be between 1 and 100")
    private int pageSize = 20;

    public Pageable toPageable() {

        if (CollectionUtils.isNotEmpty(this.getOrders())) {
            return PageRequest.of(this.page - 1, this.pageSize, this.toSort());
        }

        return PageRequest.of(this.page - 1, this.pageSize);
    }

}
