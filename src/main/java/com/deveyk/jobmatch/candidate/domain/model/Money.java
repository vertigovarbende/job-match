package com.deveyk.jobmatch.candidate.domain.model;

import com.deveyk.jobmatch.candidate.domain.exception.CandidateFieldInvalidException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
public final class Money {

    private final BigDecimal amount;
    private final String currency;

    public Money(final BigDecimal amount, final String currency) {

        if (amount == null) {
            throw new CandidateFieldInvalidException("amount", "must not be null");
        }

        if (amount.signum() < 0) {
            throw new CandidateFieldInvalidException("amount", "must not be negative");
        }

        if (currency == null || currency.isBlank()) {
            throw new CandidateFieldInvalidException("currency");
        }

        this.amount = amount;
        this.currency = currency;
    }

    public boolean sameCurrency(final Money other) {
        return other != null && this.currency.equalsIgnoreCase(other.currency);
    }

}