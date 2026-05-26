package com.CoffeeGroup.Coffee.System.features.benefit_type;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.Mappers.IBenefitTypeCreateMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.Mappers.IBenefitTypeResponseMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeCreate;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BenefitTypeService implements IBenefitTypeService {
    private final IBenefitTypeRepository benefitTypeRepository;
    private final IBenefitTypeCreateMapper createMapper;
    private final IBenefitTypeResponseMapper responseMapper;

    @Override
    public BenefitTypeResponse save(BenefitTypeCreate benefitTypeCreate) {
        validateName(benefitTypeCreate.name());
        BenefitTypeEntity benefitType = createMapper.toEntity(benefitTypeCreate);
        benefitType.setExternalId(UUID.randomUUID());
        benefitTypeRepository.save(benefitType);
        return responseMapper.toDto(benefitType);
    }

    @Override
    public BenefitTypeResponse update(UUID externalId, BenefitTypeCreate benefitTypeCreate) {
        BenefitTypeEntity benefitType = benefitTypeRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Benefit type not found"));

        validateName(benefitTypeCreate.name());

        benefitType.setName(benefitTypeCreate.name());
        benefitTypeRepository.save(benefitType);
        return responseMapper.toDto(benefitType);
    }

    @Override
    public void delete(UUID externalId) {
        BenefitTypeEntity benefitType = benefitTypeRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Benefit type not found"));
        benefitTypeRepository.delete(benefitType);
    }

    @Override
    public List<BenefitTypeResponse> all() {
        return benefitTypeRepository.findAll().stream().map(responseMapper::toDto).toList();
    }

    @Override
    public void validateName(String name) {
        if (benefitTypeRepository.existsByName(name)){
            throw new EntityAlreadyExistsException("The name "+ name + " already exists");
        }
    }
}
