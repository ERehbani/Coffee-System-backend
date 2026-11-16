package com.CoffeeGroup.Coffee.System.features.benefit_format;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.BenefitFormatResponse;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.CreateBenefitFormatRequest;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.UpdateBenefitFormatRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BenefitFormatService implements IBenefitFormatService {

    private final BenefitFormatRepository formatRepository;
    private final BenefitRepository benefitRepository;

    @Override
    public List<BenefitFormatResponse> getAll() {
        Map<BenefitType, Long> usage = usageByCode();
        return formatRepository.findAll().stream()
                .sorted(Comparator.comparing(BenefitFormatEntity::getDisplayName, String.CASE_INSENSITIVE_ORDER))
                .map(format -> toResponse(format, usage.getOrDefault(format.getCode(), 0L)))
                .toList();
    }

    @Override
    @Transactional
    public BenefitFormatResponse create(CreateBenefitFormatRequest request) {
        String displayName = request.displayName().trim();
        if (formatRepository.existsByCode(request.code())) {
            throw new EntityAlreadyExistsException("The format " + request.code() + " is already in the catalog");
        }
        if (formatRepository.existsByDisplayNameIgnoreCase(displayName)) {
            throw new EntityAlreadyExistsException(duplicateName(displayName));
        }
        BenefitFormatEntity format = BenefitFormatEntity.builder()
                .code(request.code())
                .displayName(displayName)
                .description(request.description().trim())
                .active(request.active() == null || request.active())
                .build();
        formatRepository.save(format);
        return toResponse(format, 0L);
    }

    @Override
    @Transactional
    public BenefitFormatResponse update(UUID externalId, UpdateBenefitFormatRequest request) {
        BenefitFormatEntity format = formatRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Benefit format not found"));
        String displayName = request.displayName().trim();
        if (formatRepository.existsByDisplayNameIgnoreCaseAndExternalIdNot(displayName, externalId)) {
            throw new EntityAlreadyExistsException(duplicateName(displayName));
        }
        format.setDisplayName(displayName);
        format.setDescription(request.description().trim());
        if (request.active() != null) {
            format.setActive(request.active());
        }
        return toResponse(format, usageByCode().getOrDefault(format.getCode(), 0L));
    }

    private Map<BenefitType, Long> usageByCode() {
        return benefitRepository.countBenefitsByFormatCode().stream()
                .collect(Collectors.toMap(BenefitFormatUsage::code, BenefitFormatUsage::total));
    }

    private static String duplicateName(String displayName) {
        return "A benefit type named " + displayName + " already exists";
    }

    private static BenefitFormatResponse toResponse(BenefitFormatEntity format, long usageCount) {
        return new BenefitFormatResponse(format.getExternalId(), format.getCode(), format.getDisplayName(),
                format.getDescription(), format.isActive(), usageCount);
    }
}
