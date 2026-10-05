package com.deveyk.jobmatch.shared.domain;

/**
 * Her bir modül kendi "ErrorCode" enum'unu, kendi domain paketinde tanımlar ve bu interface'i implemente edebilir.
 * Framework'ten (Spring, HTTP vb.) bağımsız tutulur: HTTP status'u bu arayüzde taşınmaz, exception ailesi belirler
 * (bkz. ADR-011 ve GlobalExceptionHandler).
 */
public interface ErrorCode {

    String code();

    String header();

    String defaultMessage();

}
