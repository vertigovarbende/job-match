package com.deveyk.jobmatch.shared.unit.presentation.exception.handler;

import ch.qos.logback.classic.Level;
import com.deveyk.jobmatch.shared.domain.exception.DomainRuleViolationException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchAuthenticationException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchConflictException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchForbiddenException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchInvalidArgumentException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchProcessException;
import com.deveyk.jobmatch.shared.domain.exception.JobMatchResourceNotFoundException;
import com.deveyk.jobmatch.shared.presentation.exception.handler.GlobalExceptionHandler;
import com.deveyk.jobmatch.shared.presentation.response.ErrorResponse;
import com.deveyk.jobmatch.shared.testsupport.SampleErrorCode;
import com.deveyk.jobmatch.shared.testsupport.SampleExceptions.SampleAuthenticationException;
import com.deveyk.jobmatch.shared.testsupport.SampleExceptions.SampleConflictException;
import com.deveyk.jobmatch.shared.testsupport.SampleExceptions.SampleDomainRuleViolationException;
import com.deveyk.jobmatch.shared.testsupport.SampleExceptions.SampleForbiddenException;
import com.deveyk.jobmatch.shared.testsupport.SampleExceptions.SampleInvalidArgumentException;
import com.deveyk.jobmatch.shared.testsupport.SampleExceptions.SampleProcessException;
import com.deveyk.jobmatch.shared.testsupport.SampleExceptions.SampleResourceNotFoundException;
import com.deveyk.jobmatch.shared.testsupport.SampleExceptions.SampleUnclassifiedException;
import com.deveyk.jobmatch.testsupport.LogTrackerConfiguration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler - Birim Testleri")
class GlobalExceptionHandlerTest extends LogTrackerConfiguration {

    private static final String EXPECTED_CODE = "SMP_001";
    private static final String EXPECTED_HEADER = "SAMPLE_ERROR";
    private static final String EXPECTED_MESSAGE = "Sample error message.";

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ===== Katman 1 - framework exception'lari =====

    @Test
    @DisplayName("handleMethodArgumentNotValidException() her FieldError icin bir subError doner, 400 statusunu bildirir ve loglar")
    void handleMethodArgumentNotValidException_returnsValidationErrorResponseWithSubErrors() throws NoSuchMethodException {

        final Method handlerMethod = GlobalExceptionHandlerTest.class.getDeclaredMethod("sampleHandlerMethod", SampleRequest.class);
        final BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new SampleRequest(""), "sampleRequest");
        bindingResult.addError(new FieldError(
                "sampleRequest",
                "name",
                "",
                false,
                new String[]{"NotBlank.sampleRequest.name", "NotBlank.name", "NotBlank.java.lang.String", "NotBlank"},
                null,
                "must not be blank"));
        final MethodArgumentNotValidException exception = new MethodArgumentNotValidException(new MethodParameter(handlerMethod, 0), bindingResult);

        final ErrorResponse response = this.handler.handleMethodArgumentNotValidException(exception);

        this.assertValidationErrorResponse(response);
        assertThat(response.getSubErrors()).hasSize(1);
        this.assertSubError(response.getSubErrors().get(0), "name", "must not be blank", "", "String");
        this.assertErrorLogged(exception, response);
        assertThat(declaredStatusOf(MethodArgumentNotValidException.class)).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    @DisplayName("handleConstraintViolationException() her ihlal icin bir subError doner, 400 statusunu bildirir ve loglar")
    void handleConstraintViolationException_returnsValidationErrorResponseWithSubErrors() {

        final ConstraintViolationException exception = new ConstraintViolationException(validate(new SampleRequest("")));

        final ErrorResponse response = this.handler.handleConstraintViolationException(exception);

        this.assertValidationErrorResponse(response);
        assertThat(response.getSubErrors()).hasSize(1);
        this.assertSubError(response.getSubErrors().get(0), "name", "must not be blank", "", "NotBlank");
        this.assertErrorLogged(exception, response);
        assertThat(declaredStatusOf(ConstraintViolationException.class)).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    @DisplayName("handleMethodArgumentTypeMismatchException() hatali parametre icin tek subError doner, 400 statusunu bildirir ve loglar")
    void handleMethodArgumentTypeMismatchException_returnsValidationErrorResponseWithSubError() {

        final MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException("abc", Long.class, "id", null, null);

        final ErrorResponse response = this.handler.handleMethodArgumentTypeMismatchException(exception);

        this.assertValidationErrorResponse(response);
        assertThat(response.getSubErrors()).hasSize(1);
        this.assertSubError(response.getSubErrors().get(0), "id", "Invalid value for field 'id'.", "abc", "Long");
        this.assertErrorLogged(exception, response);
        assertThat(declaredStatusOf(MethodArgumentTypeMismatchException.class)).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    // ===== Katman 2 ve 3 - aile bazli ve aileye oturmayan JobMatchException handler'lari =====

    @Test
    @DisplayName("handleDomainRuleViolationException() DomainRuleViolationException ailesi icin ErrorCode alanlarini korur, 422 statusunu bildirir ve loglar")
    void handleDomainRuleViolationException_returnsErrorResponseOfErrorCode_withDeclaredStatus() {

        final SampleDomainRuleViolationException exception = new SampleDomainRuleViolationException(SampleErrorCode.SAMPLE_ERROR);

        final ErrorResponse response = this.handler.handleDomainRuleViolationException(exception);

        this.assertErrorResponse(response, EXPECTED_CODE, EXPECTED_HEADER, EXPECTED_MESSAGE);
        this.assertErrorLogged(exception, response);
        assertThat(declaredStatusOf(DomainRuleViolationException.class)).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

    }

    @Test
    @DisplayName("handleJobMatchResourceNotFoundException() kaynak bulunamadi ailesi icin ErrorCode alanlarini korur, 404 statusunu bildirir ve loglar")
    void handleJobMatchResourceNotFoundException_returnsErrorResponseOfErrorCode_withDeclaredStatus() {

        final SampleResourceNotFoundException exception = new SampleResourceNotFoundException(SampleErrorCode.SAMPLE_ERROR);

        final ErrorResponse response = this.handler.handleJobMatchResourceNotFoundException(exception);

        this.assertErrorResponse(response, EXPECTED_CODE, EXPECTED_HEADER, EXPECTED_MESSAGE);
        this.assertErrorLogged(exception, response);
        assertThat(declaredStatusOf(JobMatchResourceNotFoundException.class)).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    @DisplayName("handleJobMatchConflictException() cakisma ailesi icin ErrorCode alanlarini korur, 409 statusunu bildirir ve loglar")
    void handleJobMatchConflictException_returnsErrorResponseOfErrorCode_withDeclaredStatus() {

        final SampleConflictException exception = new SampleConflictException(SampleErrorCode.SAMPLE_ERROR);

        final ErrorResponse response = this.handler.handleJobMatchConflictException(exception);

        this.assertErrorResponse(response, EXPECTED_CODE, EXPECTED_HEADER, EXPECTED_MESSAGE);
        this.assertErrorLogged(exception, response);
        assertThat(declaredStatusOf(JobMatchConflictException.class)).isEqualTo(HttpStatus.CONFLICT);

    }

    @Test
    @DisplayName("handleJobMatchForbiddenException() yetki yok ailesi icin ErrorCode alanlarini korur, 403 statusunu bildirir ve loglar")
    void handleJobMatchForbiddenException_returnsErrorResponseOfErrorCode_withDeclaredStatus() {

        final SampleForbiddenException exception = new SampleForbiddenException(SampleErrorCode.SAMPLE_ERROR);

        final ErrorResponse response = this.handler.handleJobMatchForbiddenException(exception);

        this.assertErrorResponse(response, EXPECTED_CODE, EXPECTED_HEADER, EXPECTED_MESSAGE);
        this.assertErrorLogged(exception, response);
        assertThat(declaredStatusOf(JobMatchForbiddenException.class)).isEqualTo(HttpStatus.FORBIDDEN);

    }

    @Test
    @DisplayName("handleJobMatchAuthenticationException() kimlik dogrulama ailesi icin ErrorCode alanlarini korur, 401 statusunu bildirir ve loglar")
    void handleJobMatchAuthenticationException_returnsErrorResponseOfErrorCode_withDeclaredStatus() {

        final SampleAuthenticationException exception = new SampleAuthenticationException(SampleErrorCode.SAMPLE_ERROR);

        final ErrorResponse response = this.handler.handleJobMatchAuthenticationException(exception);

        this.assertErrorResponse(response, EXPECTED_CODE, EXPECTED_HEADER, EXPECTED_MESSAGE);
        this.assertErrorLogged(exception, response);
        assertThat(declaredStatusOf(JobMatchAuthenticationException.class)).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    @DisplayName("handleJobMatchProcessException() surec ihlali ailesi icin ErrorCode alanlarini korur, 409 statusunu bildirir ve loglar")
    void handleJobMatchProcessException_returnsErrorResponseOfErrorCode_withDeclaredStatus() {

        final SampleProcessException exception = new SampleProcessException(SampleErrorCode.SAMPLE_ERROR);

        final ErrorResponse response = this.handler.handleJobMatchProcessException(exception);

        this.assertErrorResponse(response, EXPECTED_CODE, EXPECTED_HEADER, EXPECTED_MESSAGE);
        this.assertErrorLogged(exception, response);
        assertThat(declaredStatusOf(JobMatchProcessException.class)).isEqualTo(HttpStatus.CONFLICT);

    }

    @Test
    @DisplayName("handleJobMatchInvalidArgumentException() gecersiz arguman ailesi icin ErrorCode alanlarini korur, 400 statusunu bildirir ve loglar")
    void handleJobMatchInvalidArgumentException_returnsErrorResponseOfErrorCode_withDeclaredStatus() {

        final SampleInvalidArgumentException exception = new SampleInvalidArgumentException(SampleErrorCode.SAMPLE_ERROR);

        final ErrorResponse response = this.handler.handleJobMatchInvalidArgumentException(exception);

        this.assertErrorResponse(response, EXPECTED_CODE, EXPECTED_HEADER, EXPECTED_MESSAGE);
        this.assertErrorLogged(exception, response);
        assertThat(declaredStatusOf(JobMatchInvalidArgumentException.class)).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    @DisplayName("handleJobMatchException() aileye oturmayan exception icin ErrorCode alanlarini korur, 500 statusunu bildirir ve loglar")
    void handleJobMatchException_returnsErrorResponseOfErrorCode_withDeclaredStatus() {

        final SampleUnclassifiedException exception = new SampleUnclassifiedException(SampleErrorCode.SAMPLE_ERROR);

        final ErrorResponse response = this.handler.handleJobMatchException(exception);

        this.assertErrorResponse(response, EXPECTED_CODE, EXPECTED_HEADER, EXPECTED_MESSAGE);
        this.assertErrorLogged(exception, response);
        assertThat(declaredStatusOf(JobMatchException.class)).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

    }

    // ===== Katman 4 - beklenmeyen exception'lar =====

    @Test
    @DisplayName("handleException() beklenmeyen exception icin GEN_001 doner, 500 statusunu bildirir ve loglar")
    void handleException_returnsInternalServerErrorResponse_withDeclaredStatus() {

        final Exception exception = new Exception("unexpected failure");

        final ErrorResponse response = this.handler.handleException(exception);

        this.assertErrorResponse(response, "GEN_001", "INTERNAL_SERVER_ERROR", "Unexpected server error.");
        this.assertErrorLogged(exception, response);
        assertThat(declaredStatusOf(Exception.class)).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

    }

    private void assertErrorResponse(final ErrorResponse response,
                                     final String expectedCode,
                                     final String expectedHeader,
                                     final String expectedMessage) {

        assertThat(response.getTime()).isNotNull();
        assertThat(response.getCode()).isEqualTo(expectedCode);
        assertThat(response.getHeader()).isEqualTo(expectedHeader);
        assertThat(response.getMessage()).isEqualTo(expectedMessage);
        assertThat(response.getIsSuccess()).isFalse();
        assertThat(response.getSubErrors()).isNull();

    }

    private void assertValidationErrorResponse(final ErrorResponse response) {

        assertThat(response.getTime()).isNotNull();
        assertThat(response.getCode()).isEqualTo("GEN_002");
        assertThat(response.getHeader()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getMessage()).isEqualTo("Validation failed.");
        assertThat(response.getIsSuccess()).isFalse();

    }

    private void assertSubError(final ErrorResponse.SubError subError,
                                final String expectedField,
                                final String expectedMessage,
                                final String expectedValue,
                                final String expectedType) {

        assertThat(subError.getField()).isEqualTo(expectedField);
        assertThat(subError.getMessage()).isEqualTo(expectedMessage);
        assertThat(subError.getValue()).isEqualTo(expectedValue);
        assertThat(subError.getType()).isEqualTo(expectedType);

    }

    private void assertErrorLogged(final Exception exception, final ErrorResponse response) {

        final String logMessagePrefix = "responseCode:" + response.getCode();

        final Optional<String> errorLog = this.logTracker.findMessage(Level.ERROR, logMessagePrefix);
        assertThat(errorLog).hasValue(logMessagePrefix + " | " + exception.getMessage());

        final Optional<String> traceLog = this.logTracker.findMessage(Level.TRACE, logMessagePrefix);
        assertThat(traceLog).hasValue(logMessagePrefix + " | StackTrace:");

    }

    private static HttpStatus declaredStatusOf(final Class<? extends Exception> exceptionType) {

        for (final Method method : GlobalExceptionHandler.class.getDeclaredMethods()) {

            final ExceptionHandler exceptionHandler = method.getAnnotation(ExceptionHandler.class);

            if (exceptionHandler != null && Arrays.asList(exceptionHandler.value()).contains(exceptionType)) {

                final ResponseStatus responseStatus = method.getAnnotation(ResponseStatus.class);

                return responseStatus == null ? null : responseStatus.value();
            }
        }

        throw new AssertionError("No @ExceptionHandler method declared for " + exceptionType.getName());
    }

    private static <T> Set<ConstraintViolation<T>> validate(final T object) {

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            return validatorFactory.getValidator().validate(object);
        }
    }

    // MethodArgumentNotValidException'in ihtiyac duydugu MethodParameter icin kullanilir (reflection ile bulunur).
    static void sampleHandlerMethod(final SampleRequest request) {

    }

    private record SampleRequest(@NotBlank(message = "must not be blank") String name) {

    }

}
