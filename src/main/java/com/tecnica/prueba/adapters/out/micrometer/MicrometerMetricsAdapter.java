package com.tecnica.prueba.adapters.out.micrometer;

import com.tecnica.prueba.domain.port.out.MetricsPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MicrometerMetricsAdapter implements MetricsPort {

    private final MeterRegistry registry;

    private Counter clientsCreated() {
        return Counter.builder("clients.created.total")
                .description("Total clients created")
                .register(registry);
    }

    private Counter loginFailures() {
        return Counter.builder("auth.login.failures")
                .description("Total login failures")
                .register(registry);
    }

    private Timer timer(String name) {
        return Timer.builder(name)
                .publishPercentileHistogram(true)
                .register(registry);
    }

    @Override
    public void incClientsCreated() {
        clientsCreated().increment();
    }

    @Override
    public void incLoginFailures() {
        loginFailures().increment();
    }

    @Override
    public void inc(String name, Map<String, String> tags) {
        List<Tag> tagList = tags.entrySet().stream()
                .map(e -> Tag.of(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        registry.counter(name, tagList).increment();
    }


    @Override
    public <T> Mono<T> timeMono(String metricName, Mono<T> mono) {
        return Mono.defer(() -> {
            var sample = Timer.start(registry);
            return mono.doOnSuccess(v -> sample.stop(timer(metricName)))
                    .doOnError(e -> sample.stop(timer(metricName)));
        });
    }

    @Override
    public <T> Flux<T> timeFlux(String metricName, Flux<T> flux) {
        return Flux.defer(() -> {
            var sample = Timer.start(registry);
            return flux.doOnComplete(() -> sample.stop(timer(metricName)))
                    .doOnError(e -> sample.stop(timer(metricName)));
        });
    }
}