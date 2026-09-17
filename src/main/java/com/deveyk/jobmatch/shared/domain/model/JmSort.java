package com.deveyk.jobmatch.shared.domain.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JmSort {

    @Valid
    protected List<JmOrder> orders;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JmOrder {

        @NotBlank
        private String property;

        @NotNull
        private Direction direction;

    }


    public enum Direction {

        ASC,
        DESC;

        public Sort.Direction toDirection() {
            return Sort.Direction.valueOf(this.name());
        }
    }

    protected Sort toSort() {
        return Sort.by(this.orders.stream()
                        .map(order -> Sort.Order.by(order.getProperty()).with(order.getDirection().toDirection()))
                        .toList()
        );
    }

    public static JmSort of(final Sort sorts) {

        final List<JmOrder> orders = sorts.stream()
                .map(order -> JmOrder.builder()
                        .property(order.getProperty())
                        .direction(Direction.valueOf(order.getDirection().toString()))
                        .build())
                .toList();

        return JmSort.builder()
                .orders(orders)
                .build();
    }

}
