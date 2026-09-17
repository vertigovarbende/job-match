package com.deveyk.jobmatch.candidate.presentation.rest.mapper;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddCertificationCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCertificationCommand;
import com.deveyk.jobmatch.candidate.presentation.rest.request.AddCertificationRequest;
import com.deveyk.jobmatch.candidate.presentation.rest.request.UpdateCertificationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CertificationRequestMapper {

    AddCertificationCommand toCommand(AddCertificationRequest request, Long candidateId);

    UpdateCertificationCommand toCommand(UpdateCertificationRequest request, Long candidateId, Long certificationId);

}
