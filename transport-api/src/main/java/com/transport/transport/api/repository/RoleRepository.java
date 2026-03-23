package com.transport.transport.api.repository;

import com.transport.transport.api.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByLibelle(String libelle);
}
