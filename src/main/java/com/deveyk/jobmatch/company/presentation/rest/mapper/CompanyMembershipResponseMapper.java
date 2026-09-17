package com.deveyk.jobmatch.company.presentation.rest.mapper;

import com.deveyk.jobmatch.company.domain.model.CompanyMembership;
import com.deveyk.jobmatch.company.presentation.rest.response.CompanyMembershipResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * CompanyMembership (domain) -> CompanyMembershipResponse donusumu (bkz. REST.md #9). joinedAt,
 * CompanyMembership.getCreatedAt()'ten (JmBaseDomain) map edilir -- madde 2'nin "createdAt,
 * joinedAt islevi goruyor" karariyla tutarli. toResponseList, listMembers endpoint'i icin
 * (madde 8) MapStruct'in tek-obje donusumunu otomatik koleksiyona genisletmesiyle eklendi --
 * controller'da manuel .stream().map(...) yok.
 */
@Mapper(componentModel = "spring")
public interface CompanyMembershipResponseMapper {

    @Mapping(target = "joinedAt", source = "createdAt")
    CompanyMembershipResponse toResponse(CompanyMembership companyMembership);

    List<CompanyMembershipResponse> toResponseList(List<CompanyMembership> companyMemberships);

}
