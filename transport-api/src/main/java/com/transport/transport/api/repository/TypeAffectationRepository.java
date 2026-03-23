package com.transport.transport.api.repository;

import com.transport.transport.api.entity.TypeAffectation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TypeAffectationRepository extends JpaRepository<TypeAffectation, Integer> {

    List<TypeAffectation> findByActifTrue();
}
