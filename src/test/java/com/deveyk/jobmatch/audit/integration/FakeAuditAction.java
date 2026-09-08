package com.deveyk.jobmatch.audit.integration;

import com.deveyk.jobmatch.audit.domain.AuditAction;

/**
 * Testlerde kullanılan sahte {@link AuditAction} implementasyonu. Gerçek bir feature'a
 * (Job/Application/Company) bağlı olmadığından, generic audit mekanizmasını izole şekilde
 * doğrulamak için kullanılır.
 * <p>
 * İsim kasıtlı olarak {@code Fake} önekiyle başlar, {@code Test} önekiyle değil: Maven
 * Surefire/Failsafe'in varsayılan include pattern'i ({@code **&#47;Test*.java}) bu dosyayı
 * yanlışlıkla bir test sınıfı sanıp çalıştırmaya çalışmasın diye.
 */
enum FakeAuditAction implements AuditAction {

    TEST_ACTION;

    @Override
    public String code() {
        return name();
    }

    @Override
    public String description() {
        return "Test amaçlı sahte audit action";
    }

}
