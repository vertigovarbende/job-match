package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.domain.model.Certification;
import com.deveyk.jobmatch.candidate.presentation.rest.response.CertificationResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CertificationResponseMapper {

    CertificationResponse toResponse(Certification certification);

    List<CertificationResponse> toResponseList(List<Certification> certifications);

}
