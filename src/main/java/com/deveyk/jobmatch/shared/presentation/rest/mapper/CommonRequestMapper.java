package com.deveyk.jobmatch.shared.presentation.rest.mapper;

import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import com.deveyk.jobmatch.shared.presentation.rest.request.LocationRequest;
import com.deveyk.jobmatch.shared.presentation.rest.request.MoneyRequest;
import com.deveyk.jobmatch.shared.presentation.rest.request.SalaryRangeRequest;

public interface CommonRequestMapper {

    default Location toLocation(final LocationRequest request) {
        if (request == null) {
            return null;
        }

        return Location.ofNullable(request.country(), request.city());
    }

    default Money toMoney(final MoneyRequest request) {
        if (request == null) {
            return null;
        }

        return Money.ofNullable(request.amount(), request.currency());
    }

    default SalaryRange toSalaryRange(final SalaryRangeRequest request) {
        if (request == null) {
            return null;
        }

        return SalaryRange.ofNullable(this.toMoney(request.min()), this.toMoney(request.max()));
    }

}
