package com.transport.transport.api.repository;

import com.transport.transport.api.entity.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepartementRepository extends JpaRepository<Departement, Integer> {

    List<Departement> findByActifTrue();

    List<Departement> findByNomContainingIgnoreCase(String nom);
}
