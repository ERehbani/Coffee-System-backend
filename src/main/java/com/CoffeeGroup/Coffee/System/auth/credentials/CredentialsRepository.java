package com.CoffeeGroup.Coffee.System.auth.credentials;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialsRepository extends JpaRepository<CredentialsEntity, Long> {
    Optional<CredentialsEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
