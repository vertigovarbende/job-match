
package com.deveyk.jobmatch.candidate.application.port.in;

import com.deveyk.jobmatch.candidate.application.port.in.command.AttachCandidateLanguageCommand;
import com.deveyk.jobmatch.candidate.application.port.in.command.UpdateCandidateLanguageCommand;
import com.deveyk.jobmatch.candidate.domain.model.CandidateLanguage;

import java.util.List;

public interface CandidateLanguageUseCase {

    CandidateLanguage attachLanguage(AttachCandidateLanguageCommand command);

    CandidateLanguage updateLanguageProficiency(UpdateCandidateLanguageCommand command);

    void detachLanguage(Long candidateId, Long languageId);

    List<CandidateLanguage> listLanguages(Long candidateId);

}