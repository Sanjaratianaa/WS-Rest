package com.transport.transport.api.repository;

import com.transport.transport.api.entity.Authentification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthentificationRepository extends JpaRepository<Authentification, Integer> {

    Optional<Authentification> findByEmployeIdAndActifTrue(Integer employeId);

    Optional<Authentification> findByEmployeId(Integer idEmploye);

    boolean existsByEmployeId(Integer id);
}
