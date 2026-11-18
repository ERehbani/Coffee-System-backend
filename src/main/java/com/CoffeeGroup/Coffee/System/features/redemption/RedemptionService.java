package com.CoffeeGroup.Coffee.System.features.redemption;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.BenefitSoldOutException;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.exceptions.InsufficientPointsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.ProductNotAvailableException;
import com.CoffeeGroup.Coffee.System.common.persistence.Specifications;
import com.CoffeeGroup.Coffee.System.common.web.PageResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitRepository;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.mapper.RedemptionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedemptionService implements IRedemptionService {

    private final RedemptionRepository redemptionRepository;
    private final RedemptionMapper redemptionMapper;
    private final BenefitRepository benefitRepository;
    private final ClientRepository clientRepository;
    private final SecurityUtils securityUtils;
    private final Clock clock;

    @Override
    @Transactional
    public RedemptionResponse create(RedemptionCreateRequest request) {
        ClientEntity client = securityUtils.currentClient();
        BenefitEntity benefit = benefitRepository.findByExternalId(request.benefitId())
                .orElseThrow(() -> new EntityNotFoundException("Beneficio no encontrado"));

        benefit.ensureRedeemableBy(client, LocalDate.now(clock));
        ensureClientLimit(client, benefit);
        if (!client.canAfford(benefit.getPointsCost())) {
            throw new InsufficientPointsException("El cliente no tiene puntos suficientes");
        }
        if (benefitRepository.consumeRedemption(benefit.getId()) == 0) {
            throw new BenefitSoldOutException("El beneficio está agotado");
        }
        client.spendPoints(benefit.getPointsCost());

        RedemptionEntity redemption = RedemptionEntity.of(client, benefit, LocalDateTime.now(clock));
        redemptionRepository.save(redemption);
        return redemptionMapper.toResponse(redemption);
    }

    @Override
    public PageResponse<RedemptionResponse> findAll(RedemptionStatus status, Pageable pageable) {
        ClientEntity client = securityUtils.isAdmin() ? null : securityUtils.currentClient();
        return PageResponse.of(redemptionRepository.findAll(Specifications.allOf(
                        RedemptionSpecifications.ofClient(client),
                        RedemptionSpecifications.hasStatus(status)), pageable),
                redemptionMapper::toResponse);
    }

    @Override
    public RedemptionResponse getById(UUID externalId) {
        RedemptionEntity redemption = redemptionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Canje no encontrado"));
        if (!securityUtils.isAdmin() && !redemption.belongsTo(securityUtils.currentClient())) {
            throw new AccessDeniedException("Solo puedes ver tus propios canjes");
        }
        return redemptionMapper.toResponse(redemption);
    }

    @Override
    @Transactional
    public RedemptionResponse validate(String validationCode) {
        RedemptionEntity redemption = redemptionHandledByCurrentUser(validationCode);
        redemption.ensureUsableOn(LocalDate.now(clock));
        ensureProductCanBeDelivered(redemption.getBenefit());
        redemption.markUsed(LocalDateTime.now(clock));
        return redemptionMapper.toResponse(redemption);
    }

    @Override
    @Transactional
    public RedemptionResponse cancel(String validationCode) {
        RedemptionEntity redemption = redemptionHandledByCurrentUser(validationCode);
        redemption.ensureUsableOn(LocalDate.now(clock));
        redemption.cancel();
        redemption.getClient().refundPoints(redemption.getUsedPoints());
        benefitRepository.releaseRedemption(redemption.getBenefit().getId());
        return redemptionMapper.toResponse(redemption);
    }

    @Override
    public List<RedemptionResponse> getAllByClient(UUID clientExternalId) {
        ClientEntity client = clientRepository.findByExternalId(clientExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        return redemptionRepository.findAll(RedemptionSpecifications.ofClient(client)).stream()
                .map(redemptionMapper::toResponse)
                .toList();
    }

    private void ensureClientLimit(ClientEntity client, BenefitEntity benefit) {
        if (benefit.getMaxPerClient() == null) {
            return;
        }
        long redemptionsOfClient = redemptionRepository.countByClientAndBenefitAndStatusNot(
                client, benefit, RedemptionStatus.CANCELLED);
        if (benefit.reachedClientLimit(redemptionsOfClient)) {
            throw new BusinessRuleException("Ya usaste este beneficio el máximo de veces permitido");
        }
    }

    private RedemptionEntity redemptionHandledByCurrentUser(String validationCode) {
        RedemptionEntity redemption = redemptionRepository.findByValidationCode(validationCode)
                .orElseThrow(() -> new EntityNotFoundException("Canje no encontrado"));
        if (securityUtils.isAdmin()) {
            return redemption;
        }
        CafeEntity validatingCafe = securityUtils.currentCafe();
        if (!redemption.getBenefit().isUsableAt(validatingCafe)) {
            throw new AccessDeniedException("Este canje no pertenece a tu cafetería");
        }
        return redemption;
    }

    private static void ensureProductCanBeDelivered(BenefitEntity benefit) {
        if (!benefit.isProductAvailable()) {
            throw new ProductNotAvailableException(
                    "El producto de este beneficio está agotado por hoy, cancela el canje para devolver los puntos");
        }
    }
}
