package com.transport.transport.api.repository;

import com.transport.transport.api.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EmployeRepository extends JpaRepository<Employe, Integer> {

    List<Employe> findByActifTrue();

    List<Employe> findByDepartementIdAndActifTrue(Integer idDepartement);

    Optional<Employe> findByMatricule(String matricule);

    List<Employe> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}
