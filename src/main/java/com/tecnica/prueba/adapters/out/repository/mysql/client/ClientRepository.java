package com.tecnica.prueba.adapters.out.repository.mysql.client;

import com.tecnica.prueba.adapters.model.mysql.ClientModel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ClientRepository extends R2dbcRepository<ClientModel, Long> {
    Mono<ClientModel> findByName(String name);

    @Query("""
           SELECT id, name, last_name, age, birth_date, password
           FROM client_model
           ORDER BY id
           LIMIT :size OFFSET :offset
           """)
    Flux<ClientModel> findPage(int size, int offset);

    @Query("SELECT COUNT(*) FROM client_model")
    Mono<Long> countAll();
}
