package com.transport.transport.api.repository;

import com.transport.transport.api.entity.HeureTransport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HeureTransportRepository extends JpaRepository<HeureTransport, Integer> {

    List<HeureTransport> findByActifTrue();

    List<HeureTransport> findAllByOrderByHeure();
}
