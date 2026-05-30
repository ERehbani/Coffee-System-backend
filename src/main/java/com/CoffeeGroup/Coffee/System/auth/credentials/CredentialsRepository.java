package com.CoffeeGroup.Coffee.System.auth.credentials;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialsRepository extends JpaRepository<CredentialsEntity, Long> {
    Optional<CredentialsEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
