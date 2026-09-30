package com.deveyk.jobmatch.company.presentation.rest.mapper;

import com.deveyk.jobmatch.company.domain.model.Company;
import com.deveyk.jobmatch.company.presentation.rest.response.CompanyResponse;
import com.deveyk.jobmatch.shared.presentation.rest.mapper.CommonResponseMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CompanyProfileResponseMapper extends CommonResponseMapper {

    CompanyResponse toResponse(Company company);

}
