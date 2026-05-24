package com.CoffeeGroup.Coffee.System.features.purchase;


import com.CoffeeGroup.Coffee.System.common.model.Email;

import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CreateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPurchaseRepository extends JpaRepository<PurchaseEntity,Long> {

    Optional<PurchaseEntity> findByExternalId(UUID externalId);


    Optional<PurchaseEntity> findByDate(Date date);

    Optional<PurchaseEntity> findByClient(ClientEntity client);

    Optional<PurchaseEntity> findByClient(CafeEntity client);

    boolean existsByExternalId(UUID externalId);

    boolean existsByEmail(Email email);
    boolean existsByClient(ClientEntity client);


}
