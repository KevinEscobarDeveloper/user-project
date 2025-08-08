package com.tecnica.prueba.application.usercase;

import com.tecnica.prueba.application.dto.response.ClientDtoResponse;
import com.tecnica.prueba.application.dto.response.PageResponse;
import com.tecnica.prueba.application.mappers.ClientApplicationMapper;
import com.tecnica.prueba.domain.entity.Client;
import com.tecnica.prueba.domain.port.in.UserUseCase;
import com.tecnica.prueba.domain.port.out.MetricsPort;
import com.tecnica.prueba.domain.port.out.UserPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;

import static com.tecnica.prueba.adapters.configuration.utils.Constants.LIFEEXPECTEDYEARS;

@Component
@Slf4j
@AllArgsConstructor
public class UserUseCaseImpl implements UserUseCase {
    private UserPort userPort;
    private final MetricsPort metrics;

    @Override
    public Mono<ClientDtoResponse> saveClient(Client client) {
        return metrics.timeMono("clients.save.timer",
                        userPort.saveClient(client)
                                .thenReturn(ClientApplicationMapper.INSTANCE.toClientResponse(client))
                )
                .doOnSuccess(x -> metrics.incClientsCreated())
                .doOnError(e -> metrics.incLoginFailures());
    }


    @Override
    public Mono<PageResponse<ClientDtoResponse>> getClientsPage(int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        int safePage = Math.max(page, 0);
        int offset = safePage * safeSize;

        Mono<List<ClientDtoResponse>> itemsMono = userPort.findPage(safeSize, offset)
                .map(ClientApplicationMapper.INSTANCE::toClientFromDomain)
                .map(this::withDerivedFields)
                .collectList();

        Mono<Long> totalMono = userPort.countAllClients();

        return metrics.timeMono("clients.page.timer",
                Mono.zip(itemsMono, totalMono)
                        .map(t -> {
                            var items = t.getT1();
                            long total = t.getT2();
                            int totalPages = (int) Math.ceil(total / (double) safeSize);
                            metrics.inc("clients.page.requests", Map.of(
                                    "page", String.valueOf(safePage),
                                    "size", String.valueOf(safeSize),
                                    "resultCount", String.valueOf(items.size())
                            ));

                            return PageResponse.<ClientDtoResponse>builder()
                                    .items(items)
                                    .page(safePage)
                                    .size(safeSize)
                                    .totalElements(total)
                                    .totalPages(totalPages)
                                    .hasNext(safePage + 1 < totalPages)
                                    .hasPrevious(safePage > 0)
                                    .build();
                        })
                        .doOnError(e -> metrics.inc("clients.page.errors", Map.of(
                                "page", String.valueOf(safePage),
                                "size", String.valueOf(safeSize)
                        )))
        );
    }


    private ClientDtoResponse withDerivedFields(ClientDtoResponse dto) {
        LocalDate birth = dto.getBirthDate();
        int livedYears = (birth != null)
                ? Period.between(birth, LocalDate.now()).getYears()
                : (dto.getAge() != null ? dto.getAge() : 0);

        int remaining = Math.max(LIFEEXPECTEDYEARS - livedYears, 0);
        LocalDate estimated = (birth != null) ? birth.plusYears(LIFEEXPECTEDYEARS) : null;

        return dto.toBuilder()
                .estimatedDeathDate(estimated)
                .yearsRemaining(remaining)
                .build();
    }
}
