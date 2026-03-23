package com.transport.transport.api.repository;

import com.transport.transport.api.entity.DateTransport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DateTransportRepository extends JpaRepository<DateTransport, Integer> {

    List<DateTransport> findByActifTrue();

    Optional<DateTransport> findByDateJour(LocalDate dateJour);
}
