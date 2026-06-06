package com.CoffeeGroup.Coffee.System.features.benefit_type;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.Mappers.IBenefitTypeCreateMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.Mappers.IBenefitTypeResponseMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeCreate;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BenefitTypeService implements IBenefitTypeService {
    private final IBenefitTypeRepository benefitTypeRepository;
    private final IBenefitTypeResponseMapper responseMapper;
    private final SecurityUtils securityUtils;

    @Override
    public List<BenefitTypeResponse> getAll() {
        if (securityUtils.isCafeteria()){
        }
        return benefitTypeRepository.findAll().stream().map(responseMapper::toDto).toList();
    }

    public void validateName(String name) {
        if (benefitTypeRepository.existsByName(name)){
            throw new EntityAlreadyExistsException("The name "+ name + " already exists");
        }
    }
}
