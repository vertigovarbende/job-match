
package com.deveyk.jobmatch.candidate.application.port.in;

import com.deveyk.jobmatch.candidate.application.port.in.command.AddCertificationCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCertificationCommand;
import com.deveyk.jobmatch.candidate.domain.model.Certification;

import java.util.List;

public interface CertificationUseCase {

    Certification addCertification(AddCertificationCommand command);

    Certification updateCertification(UpdateCertificationCommand command);

    void deleteCertification(Long candidateId, Long certificationId);

    List<Certification> listCertifications(Long candidateId);

}