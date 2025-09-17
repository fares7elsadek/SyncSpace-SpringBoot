package com.fares7elsadek.syncspace.user.infrastructure.repository;

import com.fares7elsadek.syncspace.user.domain.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, String> {
    Optional<Roles> findByName(String name);
}
