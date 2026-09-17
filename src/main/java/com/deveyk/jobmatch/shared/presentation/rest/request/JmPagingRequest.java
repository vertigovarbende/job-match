package com.deveyk.jobmatch.shared.presentation.rest.request;

import com.deveyk.jobmatch.shared.domain.model.JmSort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public abstract class JmPagingRequest {

    @Valid
    @NotNull
    protected JmPageable pageable;

    public abstract boolean isOrderPropertyAccepted();

    public boolean isPropertyAccepted(final Set<String> acceptedProperties) {

        if (this.pageable == null || CollectionUtils.isEmpty(this.pageable.getOrders())) {
            return true;
        }

        final List<JmSort.JmOrder> orders = this.pageable.getOrders();

        final boolean hasInvalidOrder = orders.stream()
                .anyMatch(order -> StringUtils.isBlank(order.getProperty()) || order.getDirection() == null);

        if (hasInvalidOrder) {
            return true;
        }

        return orders.stream()
                .map(JmSort.JmOrder::getProperty)
                .allMatch(acceptedProperties::contains);
    }

}
