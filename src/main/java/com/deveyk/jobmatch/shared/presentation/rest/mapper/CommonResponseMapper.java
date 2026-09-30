package com.deveyk.jobmatch.shared.presentation.rest.mapper;

import com.deveyk.jobmatch.shared.domain.model.Location;
import com.deveyk.jobmatch.shared.domain.model.Money;
import com.deveyk.jobmatch.shared.domain.model.SalaryRange;
import com.deveyk.jobmatch.shared.presentation.rest.response.LocationResponse;
import com.deveyk.jobmatch.shared.presentation.rest.response.MoneyResponse;
import com.deveyk.jobmatch.shared.presentation.rest.response.SalaryRangeResponse;

public interface CommonResponseMapper {

    default LocationResponse toLocationResponse(final Location location) {
        if (location == null) {
            return null;
        }

        return new LocationResponse(location.getCountry(), location.getCity());
    }

    default MoneyResponse toMoneyResponse(final Money money) {
        if (money == null) {
            return null;
        }

        return new MoneyResponse(money.getAmount(), money.getCurrency());
    }

    default SalaryRangeResponse toSalaryRangeResponse(final SalaryRange salaryRange) {
        if (salaryRange == null) {
            return null;
        }

        return new SalaryRangeResponse(this.toMoneyResponse(salaryRange.getMin()), this.toMoneyResponse(salaryRange.getMax()));
    }

}
