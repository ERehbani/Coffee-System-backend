package com.CoffeeGroup.Coffee.System.features.benefit_type;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.Mappers.IBenefitTypeCreateMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.Mappers.IBenefitTypeResponseMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeCreate;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeResponse;
import jakarta.persistence.EntityNotFoundException;
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
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el tipo beneficio"));

        validateName(benefitTypeCreate.name());

        benefitType.setName(benefitTypeCreate.name());
        benefitTypeRepository.save(benefitType);
        return responseMapper.toDto(benefitType);
    }

    @Override
    public void delete(UUID externalId) {
        BenefitTypeEntity benefitType = benefitTypeRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el tipo beneficio"));
        benefitTypeRepository.delete(benefitType);
    }

    @Override
    public List<BenefitTypeResponse> all() {
        return benefitTypeRepository.findAll().stream().map(responseMapper::toDto).toList();
    }

    @Override
    public void validateName(String name) {
        if (benefitTypeRepository.existsByName(name)){
            throw new EntityNotFoundException("El nombre "+ name + " ya existe");
        }
    }
}
