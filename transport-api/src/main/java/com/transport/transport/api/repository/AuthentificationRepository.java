package com.transport.transport.api.repository;

import com.transport.transport.api.entity.Authentification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;
import java.util.Optional;

public interface AuthentificationRepository extends JpaRepository<Authentification, Integer> {

    Optional<Authentification> findByEmailAndActifTrue(String email);

    Optional<Authentification> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Authentification> findByEmployeId(Integer idEmploye);

    boolean existsByEmployeId(Integer id);
}
