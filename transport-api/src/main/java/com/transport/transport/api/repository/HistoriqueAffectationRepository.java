package com.transport.transport.api.repository;

import com.transport.transport.api.entity.HistoriqueAffectation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoriqueAffectationRepository extends JpaRepository<HistoriqueAffectation, Integer> {

    List<HistoriqueAffectation> findByAffectationIdOrderByDateModificationDesc(Integer idAffectation);
}
