package com.deveyk.jobmatch.catalog.presentation.rest.request;

import com.deveyk.jobmatch.shared.presentation.rest.request.JmPagingRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LanguageSearchRequest extends JmPagingRequest {

    private static final Set<String> ACCEPTED_ORDER_PROPERTIES = Set.of("id", "name");

    @Size(min = 1, max = 100)
    private String name;

    @JsonIgnore
    @AssertTrue
    @Override
    public boolean isOrderPropertyAccepted() {
        return this.isPropertyAccepted(ACCEPTED_ORDER_PROPERTIES);
    }

}
