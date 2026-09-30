package com.deveyk.jobmatch.shared.unit.domain;

import com.deveyk.jobmatch.shared.domain.Guard;
import com.deveyk.jobmatch.shared.domain.exception.FieldInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Guard - Birim Testleri")
class GuardTest {

    @Test
    @DisplayName("requireNonBlank() geçerli bir değer verildiğinde değeri aynen döner")
    void requireNonBlank_returnsValue_whenValueIsNonBlank() {
        final String result = Guard.requireNonBlank("Ankara", "city");

        assertThat(result).isEqualTo("Ankara");
    }

    @Test
    @DisplayName("requireNonBlank() değer null olduğunda FieldInvalidException fırlatır")
    void requireNonBlank_throwsFieldInvalidException_whenValueIsNull() {
        assertThatThrownBy(() -> Guard.requireNonBlank(null, "city"))
                .isInstanceOf(FieldInvalidException.class)
                .hasMessageContaining("city");
    }

    @Test
    @DisplayName("requireNonBlank() değer boş (blank) olduğunda FieldInvalidException fırlatır")
    void requireNonBlank_throwsFieldInvalidException_whenValueIsBlank() {
        assertThatThrownBy(() -> Guard.requireNonBlank("   ", "city"))
                .isInstanceOf(FieldInvalidException.class)
                .hasMessageContaining("city");
    }

}
