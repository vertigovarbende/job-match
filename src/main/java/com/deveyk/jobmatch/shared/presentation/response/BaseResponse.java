package com.deveyk.jobmatch.shared.presentation.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class BaseResponse<T> {

    @Builder.Default
    private LocalDateTime time = LocalDateTime.now();

    @Builder.Default
    private String code = UUID.randomUUID().toString();

    private Boolean success;

    private T response;

    // ----- WITHOUT BODY -----
    public static <T> BaseResponse<T> success() {
        return BaseResponse.<T>builder()
                .success(true)
                .build();
    }

    // ----- WITH BODY -----
    public static <T> BaseResponse<T> success(final T response) {
        return BaseResponse.<T>builder()
                .success(true)
                .response(response)
                .build();
    }

}
