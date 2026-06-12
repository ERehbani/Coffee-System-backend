package com.CoffeeGroup.Coffee.System.features.cafe;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeRepository extends JpaRepository <CafeEntity, Long> {

    Optional<CafeEntity> findByExternalId(UUID externalId);

    Optional<CafeEntity> findByEmail(Email email);

    boolean existsByExternalId(UUID externalId);

    boolean existsByEmail(Email email);

    Optional<CafeEntity> findByUser(UserEntity user);
    Optional<CafeEntity> findByExternalIdAndUser_Credentials_Username(UUID externalId, String username);

    List<CafeEntity> findByNameContainingIgnoreCase(String name);

    List<CafeEntity> findByAddressContainingIgnoreCase(String address);

    List<CafeEntity> findByNameContainingIgnoreCaseAndAddressContainingIgnoreCase(String name, String address);

    List<CafeEntity> findByState(Boolean state);

    List<CafeEntity> findByNameContainingIgnoreCaseAndState(String name, Boolean state);

    List<CafeEntity> findByAddressContainingIgnoreCaseAndState(String address, Boolean state);

    List<CafeEntity> findByNameContainingIgnoreCaseAndAddressContainingIgnoreCaseAndState(String name, String address, Boolean state);

}
