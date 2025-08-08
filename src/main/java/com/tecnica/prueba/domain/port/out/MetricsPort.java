package com.tecnica.prueba.domain.port.out;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface MetricsPort {

    void incClientsCreated();

    void incLoginFailures();

    <T> Mono<T> timeMono(String metricName, Mono<T> mono);


    void inc(String name, Map<String, String> tags);
}