package com.deveyk.jobmatch.catalog.presentation.rest;

import com.deveyk.jobmatch.catalog.application.port.in.LanguageUseCase;
import com.deveyk.jobmatch.catalog.domain.model.Language;
import com.deveyk.jobmatch.catalog.presentation.rest.mapper.LanguageResponseMapper;
import com.deveyk.jobmatch.catalog.presentation.rest.request.LanguageSearchRequest;
import com.deveyk.jobmatch.catalog.presentation.rest.response.LanguageResponse;
import com.deveyk.jobmatch.shared.domain.exception.FieldInvalidException;
import com.deveyk.jobmatch.shared.domain.model.JmPage;
import com.deveyk.jobmatch.shared.presentation.response.BaseResponse;
import com.deveyk.jobmatch.shared.presentation.response.JmPageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageUseCase languageUseCase;
    private final LanguageResponseMapper languageResponseMapper;

    @GetMapping(CatalogApiPaths.LANGUAGES)
    public BaseResponse<JmPageResponse<LanguageResponse>> listLanguages(@Valid final LanguageSearchRequest request) {

        if (!request.isOrderPropertyAccepted()) {
            throw new FieldInvalidException("sort", "must be one of the accepted properties");
        }

        final JmPage<Language> page = this.languageUseCase.listLanguages(request.getName(), request.getPageable().toPageable());
        final var content = this.languageResponseMapper.toResponseList(page.getContent());

        final JmPageResponse<LanguageResponse> response = JmPageResponse.<LanguageResponse>builder()
                .of(page, content)
                .build();

        return BaseResponse.success(response);
    }

}
