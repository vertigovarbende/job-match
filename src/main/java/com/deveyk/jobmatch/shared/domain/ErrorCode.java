package com.deveyk.jobmatch.shared.domain;

/**
 * Her bir modül kendi "ErrorCode" enum'unu, kendi domain paketinde tanımlar ve bu interface'i implemente edebilir.
 * Framework'ten (Spring, HTTP vb.) bağımsız tutulur; presentation katmanı status() değerini kendi ihtiyacına göre yorumlar.
 */
public interface ErrorCode {

    String code();

    String header();

    int status();

    String defaultMessage();

}
