package com.deveyk.jobmatch.shared.testsupport;

import com.deveyk.jobmatch.shared.domain.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;


@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum SampleErrorCode implements ErrorCode {

    SAMPLE_ERROR(
            "SMP_001",
            "SAMPLE_ERROR",
            "Sample error message."
    );

    private final String code;
    private final String header;
    private final String defaultMessage;

}
