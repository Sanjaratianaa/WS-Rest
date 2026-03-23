package com.transport.transport.api.repository;

import com.transport.transport.api.entity.TypeTransport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TypeTransportRepository extends JpaRepository<TypeTransport, Integer> {

    List<TypeTransport> findByActifTrue();
}
