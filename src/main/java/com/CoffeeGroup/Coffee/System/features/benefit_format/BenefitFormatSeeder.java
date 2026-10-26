package com.CoffeeGroup.Coffee.System.features.benefit_format;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;

import lombok.RequiredArgsConstructor;

/** Seeds the initial catalog. It only inserts the codes that are missing, so rerunning it keeps admin edits. */
@Component
@RequiredArgsConstructor
public class BenefitFormatSeeder {

    private final IBenefitFormatRepository repository;

    public void seed() {
        seedIfMissing(BenefitType.CODIGO_DESCUENTO, "Descuento",
                "Reduce el precio final del pedido en un porcentaje o monto fijo.");
        seedIfMissing(BenefitType.PRODUCTO_GRATIS_SOCIO, "Producto gratis",
                "Entrega un producto del catálogo sin costo en puntos adicional.");
        seedIfMissing(BenefitType.DOS_POR_UNO, "2x1",
                "Entrega dos unidades del mismo producto al costo de una.");
    }

    private void seedIfMissing(BenefitType code, String displayName, String description) {
        if (repository.existsByCode(code)) {
            return;
        }
        repository.save(BenefitFormatEntity.builder()
                .externalId(UUID.randomUUID())
                .code(code)
                .displayName(displayName)
                .description(description)
                .active(true)
                .build());
    }
}
