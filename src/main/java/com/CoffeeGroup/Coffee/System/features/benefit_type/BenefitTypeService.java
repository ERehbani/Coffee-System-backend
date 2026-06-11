package com.CoffeeGroup.Coffee.System.features.benefit_type;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.Mappers.IBenefitTypeResponseMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeCreate;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.product.IProductRepository;
import com.CoffeeGroup.Coffee.System.features.product.ProductEntity;
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
    private final IProductRepository productRepository;
    private final CafeRepository cafeRepository;


    @Override
    public List<BenefitTypeResponse> getAll() {
        return benefitTypeRepository.findAll().stream().map(responseMapper::toDto).toList();
    }

    @Override
    public BenefitTypeResponse create(BenefitTypeCreate benefitTypeCreate){

        if (securityUtils.isCafeteria() &&
                benefitTypeCreate.benefitType().equals(BenefitType.PRODUCTO_GRATIS_SOCIO)) {
            throw new IllegalArgumentException("La cafetería no puede crear este tipo de beneficio");
        }
        BenefitTypeEntity type = new BenefitTypeEntity();
        type.setExternalId(UUID.randomUUID());
        type.setName(benefitTypeCreate.benefitType());

        if (benefitTypeCreate.cafeEntity() != null) {
            CafeEntity cafe = cafeRepository.findByExternalId(benefitTypeCreate.cafeEntity())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe no encontrado"));
            type.setCafeEntity(cafe);
        }

        if (benefitTypeCreate.benefitType().equals(BenefitType.CODIGO_DESCUENTO)) {
            if (benefitTypeCreate.porcentajeDescuento() == null) {
                throw new IllegalArgumentException("Debe especificar el porcentaje de descuento");
            }
            type.setPorcentajeDescuento(benefitTypeCreate.porcentajeDescuento());

        } else {
            if (benefitTypeCreate.product() == null) {
                throw new IllegalArgumentException("Debe especificar un producto");
            }
            ProductEntity producto = productRepository.findByExternalIdAndCafeExternalId(benefitTypeCreate.product(),benefitTypeCreate.cafeEntity())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
            type.setProduct(producto);
        }

        benefitTypeRepository.save(type);
        return responseMapper.toDto(type);
    }

    public void validateName(String name) {
        if (benefitTypeRepository.existsByName(name)){
            throw new EntityAlreadyExistsException("The name "+ name + " already exists");
        }
    }
}
