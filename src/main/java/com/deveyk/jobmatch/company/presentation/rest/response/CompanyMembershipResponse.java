package com.deveyk.jobmatch.company.presentation.rest.response;

import java.time.LocalDateTime;

public record CompanyMembershipResponse(

        Long id,

        Long companyId,

        Long userId,

        LocalDateTime joinedAt

) {

}
