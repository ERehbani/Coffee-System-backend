package com.CoffeeGroup.Coffee.System.features.benefit_format;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.BenefitFormatResponse;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.CreateBenefitFormatRequest;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.UpdateBenefitFormatRequest;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BenefitFormatService implements IBenefitFormatService {

    private final IBenefitFormatRepository repository;

    @Override
    public List<BenefitFormatResponse> getAll() {
        Map<BenefitType, Long> usage = new HashMap<>();
        repository.countBenefitsByFormatCode().forEach(row -> usage.put(row.code(), row.total()));

        return repository.findAll().stream()
                .sorted(Comparator.comparing(BenefitFormatEntity::getDisplayName, String.CASE_INSENSITIVE_ORDER))
                .map(format -> toResponse(format, usage.getOrDefault(format.getCode(), 0L)))
                .toList();
    }

    @Override
    public BenefitFormatResponse create(CreateBenefitFormatRequest request) {
        String displayName = request.displayName().trim();
        if (repository.existsByCode(request.code())) {
            throw new EntityAlreadyExistsException("The format " + request.code() + " is already in the catalog");
        }
        if (repository.existsByDisplayNameIgnoreCase(displayName)) {
            throw new EntityAlreadyExistsException(duplicateName(displayName));
        }

        BenefitFormatEntity format = BenefitFormatEntity.builder()
                .externalId(UUID.randomUUID())
                .code(request.code())
                .displayName(displayName)
                .description(request.description().trim())
                .active(request.active() == null || request.active())
                .build();
        repository.save(format);
        return toResponse(format, 0L);
    }

    @Override
    public BenefitFormatResponse update(UUID externalId, UpdateBenefitFormatRequest request) {
        BenefitFormatEntity format = repository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Benefit format not found"));
        String displayName = request.displayName().trim();
        if (repository.existsByDisplayNameIgnoreCaseAndExternalIdNot(displayName, externalId)) {
            throw new EntityAlreadyExistsException(duplicateName(displayName));
        }

        format.setDisplayName(displayName);
        format.setDescription(request.description().trim());
        if (request.active() != null) {
            format.setActive(request.active());
        }
        repository.save(format);
        return toResponse(format, countUsage(format.getCode()));
    }

    private long countUsage(BenefitType code) {
        return repository.countBenefitsByFormatCode().stream()
                .filter(row -> row.code() == code)
                .mapToLong(BenefitFormatUsage::total)
                .sum();
    }

    private static String duplicateName(String displayName) {
        return "A benefit type named " + displayName + " already exists";
    }

    private static BenefitFormatResponse toResponse(BenefitFormatEntity format, long usageCount) {
        return new BenefitFormatResponse(format.getExternalId(), format.getCode(), format.getDisplayName(),
                format.getDescription(), format.getActive(), usageCount);
    }
}
