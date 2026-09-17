package com.deveyk.jobmatch.catalog.presentation.rest;

import com.deveyk.jobmatch.catalog.application.port.in.SkillUseCase;
import com.deveyk.jobmatch.catalog.domain.model.Skill;
import com.deveyk.jobmatch.catalog.presentation.rest.mapper.SkillResponseMapper;
import com.deveyk.jobmatch.catalog.presentation.rest.request.SkillSearchRequest;
import com.deveyk.jobmatch.catalog.presentation.rest.response.SkillResponse;
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
public class SkillController {

    private final SkillUseCase skillUseCase;
    private final SkillResponseMapper skillResponseMapper;

    @GetMapping(CatalogApiPaths.SKILLS)
    public BaseResponse<JmPageResponse<SkillResponse>> listSkills(@Valid final SkillSearchRequest request) {

        if (!request.isOrderPropertyAccepted()) {
            throw new FieldInvalidException("sort", "must be one of the accepted properties");
        }

        final JmPage<Skill> page = this.skillUseCase.listSkills(request.getName(), request.getPageable().toPageable());
        final var content = this.skillResponseMapper.toResponseList(page.getContent());

        final JmPageResponse<SkillResponse> response = JmPageResponse.<SkillResponse>builder()
                .of(page, content)
                .build();

        return BaseResponse.success(response);
    }

}
