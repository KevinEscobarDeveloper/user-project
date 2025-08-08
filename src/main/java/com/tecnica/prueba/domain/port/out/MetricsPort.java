package com.tecnica.prueba.domain.port.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface MetricsPort {

    void incClientsCreated();

    void incLoginFailures();

    <T> Mono<T> timeMono(String metricName, Mono<T> mono);

    <T> Flux<T> timeFlux(String metricName, Flux<T> flux);

    void inc(String name, Map<String, String> tags);
}