package com.deveyk.jobmatch.testsupport;

import com.deveyk.jobmatch.shared.presentation.response.BaseResponse;
import com.deveyk.jobmatch.shared.presentation.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestComponent
@RequiredArgsConstructor
public class JobMatchMockMvc {

    private final MockMvc mockMvc;

    public ResultActions perform(final MockHttpServletRequestBuilder requestBuilder) throws Exception {

        return this.mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    public ResultActions perform(final MockHttpServletRequestBuilder requestBuilder,
                                  final HttpStatus expectedStatus,
                                  final BaseResponse<?> mockResponse) throws Exception {

        return this.mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(jsonPath("$.time").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.success").value(mockResponse.getSuccess()));
    }

    public ResultActions perform(final MockHttpServletRequestBuilder requestBuilder,
                                  final HttpStatus expectedStatus,
                                  final ErrorResponse mockErrorResponse) throws Exception {

        return this.mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(jsonPath("$.time").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.code").value(mockErrorResponse.getCode()))
                .andExpect(jsonPath("$.header").isString())
                .andExpect(jsonPath("$.header").value(mockErrorResponse.getHeader()))
                .andExpect(jsonPath("$.isSuccess").isBoolean())
                .andExpect(jsonPath("$.isSuccess").value(Boolean.FALSE));
    }

}
