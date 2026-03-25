package com.transport.transport.api.repository;

import com.transport.transport.api.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehiculeRepository extends JpaRepository<Vehicule, Integer> {

    List<Vehicule> findByActifTrue();

    boolean existsByMatriculeAndActifTrue(String matricule);

    boolean existsByMatriculeAndActifTrueAndIdNot(String matricule, Integer id);
}
