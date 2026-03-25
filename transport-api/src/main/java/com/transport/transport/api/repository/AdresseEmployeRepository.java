package com.transport.transport.api.repository;

import com.transport.transport.api.entity.AdresseEmploye;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AdresseEmployeRepository extends JpaRepository<AdresseEmploye, Integer> {

    List<AdresseEmploye> findByEmployeIdAndActifTrue(Integer idEmploye);

    Optional<AdresseEmploye> findByEmployeIdAndEstPrincipaleTrue(Integer idEmploye);

    Optional<AdresseEmploye> findByEmployeIdAndEstPrincipaleTrueAndActifTrue(Integer idEmploye);
}
