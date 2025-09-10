package com.fares7elsadek.syncspace.user.repository;

import com.fares7elsadek.syncspace.user.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles, String> {
}
