package com.CoffeeGroup.Coffee.System.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.permissions.PermitEntity;
import com.CoffeeGroup.Coffee.System.auth.permissions.PermitRepository;
import com.CoffeeGroup.Coffee.System.auth.permissions.Permits;
import com.CoffeeGroup.Coffee.System.auth.permissions.RoleEntity;
import com.CoffeeGroup.Coffee.System.auth.permissions.RoleRepository;
import com.CoffeeGroup.Coffee.System.auth.permissions.Roles;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.IBenefitRepository;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.Enum.GlobalBenefit;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.IBenefitTypeRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;
import com.CoffeeGroup.Coffee.System.features.category.ICategoryRepository;
import com.CoffeeGroup.Coffee.System.features.product.IProductRepository;
import com.CoffeeGroup.Coffee.System.features.product.ProductEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserRepository;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermitRepository permitRepository;
    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final TierRepository tierRepository;
    private final ICategoryRepository categoryRepository;
    private final CafeRepository cafeRepository;
    private final IProductRepository productRepository;
    private final IBenefitTypeRepository benefitTypeRepository;
    private final IBenefitRepository benefitRepository;

    @Value("${app.superadmin.email}")
    private String superAdminEmail;

    @Value("${app.superadmin.password}")
    private String superAdminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        Map<Permits, PermitEntity> permits = createPermits();

        createRole(
                Roles.ROLE_CLIENTE,
                Set.of(
                        Permits.VER_PRODUCTOS,
                        Permits.CREAR_COMPRA,
                        Permits.VER_COMPRAS_PROPIAS
                ),
                permits
        );

        RoleEntity cafeteriaRole = createRole(
                Roles.ROLE_CAFETERIA,
                Set.of(
                        Permits.VER_PRODUCTOS,
                        Permits.CREAR_PRODUCTOS,
                        Permits.ACTUALIZAR_PRODUCTOS,
                        Permits.VER_COMPRAS_CAFETERIA
                ),
                permits
        );

        RoleEntity superAdminRole = createRole(
                Roles.ROLE_SUPERADMIN,
                Set.of(Permits.values()),
                permits
        );

        createInitialTiers();

        createInitialCategories();

        createSuperAdmin(superAdminRole);

        createDemoData(cafeteriaRole);
    }

    private Map<Permits, PermitEntity> createPermits() {
        Map<Permits, PermitEntity> permitsMap = new HashMap<>();

        for (Permits permit : Permits.values()) {
            PermitEntity permitEntity = permitRepository.findByPermit(permit)
                    .orElseGet(() -> permitRepository.save(
                            PermitEntity.builder()
                                    .permit(permit)
                                    .build()
                    ));

            permitsMap.put(permit, permitEntity);
        }

        return permitsMap;
    }

    private RoleEntity createRole(
            Roles roleName,
            Set<Permits> permits,
            Map<Permits, PermitEntity> permitsMap
    ) {
        RoleEntity role = roleRepository.findByRole(roleName)
                .orElseGet(() -> roleRepository.save(new RoleEntity(roleName)));

        for (Permits permit : permits) {
            role.addPermit(permitsMap.get(permit));
        }

        return roleRepository.save(role);
    }

    private void createSuperAdmin(RoleEntity superAdminRole) {
        if (credentialsRepository.existsByUsername(superAdminEmail)) {
            return;
        }

        Email email = new Email(superAdminEmail);

        UserEntity user = UserEntity.builder()
                .externalId(UUID.randomUUID())
                .email(email)
                .state(true)
                .creationDate(new Date())
                .build();

        UserEntity savedUser = userRepository.save(user);

        CredentialsEntity credentials = CredentialsEntity.builder()
                .username(superAdminEmail)
                .password(passwordEncoder.encode(superAdminPassword))
                .enabled(true)
                .usuario(savedUser)
                .roles(Set.of(superAdminRole))
                .build();

        credentialsRepository.save(credentials);
    }

    private void createInitialTiers() {
        createTierIfNotExists("BRONCE", 0);
        createTierIfNotExists("PLATA", 1000);
        createTierIfNotExists("ORO", 3000);
        createTierIfNotExists("DIAMANTE", 7000);
    }

    private void createTierIfNotExists(String name, Integer minPoints) {
        if (tierRepository.findByName(name).isPresent()) {
            return;
        }

        TierEntity tier = TierEntity.builder()
                .externalId(UUID.randomUUID())
                .name(name)
                .minPoints(minPoints)
                .build();

        tierRepository.save(tier);
    }

    private void createInitialCategories() {
        List<String> categories = List.of(
                "Cafe",
                "Tes e Infusiones",
                "Pasteleria",
                "Sandwiches",
                "Bebidas Frias"
        );

        for (String name : categories) {
            createCategoryIfNotExists(name);
        }
    }

    private CategoryEntity createCategoryIfNotExists(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(
                        CategoryEntity.builder()
                                .externalId(UUID.randomUUID())
                                .name(name)
                                .build()
                ));
    }

    private void createDemoData(RoleEntity cafeteriaRole) {
        if (cafeRepository.existsByEmail(new Email("cafe.central@test.com"))) {
            return;
        }

        CategoryEntity cafeCategory = createCategoryIfNotExists("Cafe");
        CategoryEntity pastryCategory = createCategoryIfNotExists("Pasteleria");

        TierEntity bronceTier = tierRepository.findByName("BRONCE").orElse(null);
        if (bronceTier == null) {
            return;
        }

        CafeEntity cafeCentral = createCafeIfNotExists(
                cafeteriaRole,
                "cafe.central@test.com", "Cafe1234",
                "Cafe Central", "Av Siempreviva 123",
                "Cafeteria de prueba en el centro",
                "+54 11 1111-1111", "Lun a Vie 8-20");

        CafeEntity cafeNorte = createCafeIfNotExists(
                cafeteriaRole,
                "cafe.norte@test.com", "Cafe1234",
                "Cafe Norte", "Calle Falsa 456",
                "Cafeteria de prueba zona norte",
                "+54 11 2222-2222", "Lun a Dom 9-22");

        ProductEntity capuccino = createProductIfNotExists(
                cafeCentral, cafeCategory, "Capuccino", "Capuccino clasico",
                new BigDecimal("2500"), 80);
        createProductIfNotExists(
                cafeCentral, pastryCategory, "Medialuna", "Medialuna de manteca",
                new BigDecimal("800"), 50);

        createProductIfNotExists(
                cafeNorte, cafeCategory, "Latte", "Cafe latte grande",
                new BigDecimal("2800"), 90);
        createProductIfNotExists(
                cafeNorte, pastryCategory, "Brownie", "Brownie con nueces",
                new BigDecimal("1500"), 60);

        BenefitTypeEntity discountType = createBenefitType(
                BenefitType.CODIGO_DESCUENTO, 15.0, null, null);
        BenefitTypeEntity pointsType = createBenefitType(
                BenefitType.PRODUCTO_POR_PUNTOS, null, cafeCentral, capuccino);

        createBenefit(
                "Descuento 15% bienvenida", "Descuento de bienvenida para socios",
                500, discountType, bronceTier, null,
                GlobalBenefit.GLOBAL_BENEFIT);

        createBenefit(
                "Capuccino por puntos", "Cambia tus puntos por un capuccino",
                300, pointsType, bronceTier, cafeCentral,
                GlobalBenefit.CAFFETERIA_BENEFIT);
    }

    private CafeEntity createCafeIfNotExists(
            RoleEntity cafeteriaRole,
            String email,
            String password,
            String name,
            String address,
            String description,
            String phoneNumber,
            String schedule
    ) {
        Email cafeEmail = new Email(email);
        if (cafeRepository.existsByEmail(cafeEmail)) {
            return cafeRepository.findByEmail(cafeEmail).orElseThrow();
        }

        UserEntity user = userRepository.save(UserEntity.builder()
                .externalId(UUID.randomUUID())
                .email(cafeEmail)
                .state(true)
                .creationDate(new Date())
                .build());

        credentialsRepository.save(CredentialsEntity.builder()
                .username(email)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .usuario(user)
                .roles(Set.of(cafeteriaRole))
                .build());

        return cafeRepository.save(CafeEntity.builder()
                .externalId(UUID.randomUUID())
                .name(name)
                .address(address)
                .email(cafeEmail)
                .phoneNumber(phoneNumber)
                .description(description)
                .state(true)
                .image(null)
                .schedule(schedule)
                .user(user)
                .build());
    }

    private ProductEntity createProductIfNotExists(
            CafeEntity cafe,
            CategoryEntity category,
            String name,
            String description,
            BigDecimal price,
            Integer awardedPoints
    ) {
        return productRepository.findByName(name)
                .orElseGet(() -> productRepository.save(
                        ProductEntity.builder()
                                .externalId(UUID.randomUUID())
                                .cafe(cafe)
                                .category(category)
                                .name(name)
                                .description(description)
                                .price(price)
                                .awardedPoints(awardedPoints)
                                .state(true)
                                .build()
                ));
    }

    private BenefitTypeEntity createBenefitType(
            BenefitType name,
            Double porcentajeDescuento,
            CafeEntity cafe,
            ProductEntity product
    ) {
        return benefitTypeRepository.save(BenefitTypeEntity.builder()
                .externalId(UUID.randomUUID())
                .name(name)
                .porcentajeDescuento(porcentajeDescuento)
                .cafeEntity(cafe)
                .product(product)
                .build());
    }

    private void createBenefit(
            String name,
            String description,
            Integer pointsCost,
            BenefitTypeEntity benefitType,
            TierEntity tier,
            CafeEntity cafe,
            GlobalBenefit isGlobal
    ) {
        benefitRepository.save(BenefitEntity.builder()
                .externalId(UUID.randomUUID())
                .name(name)
                .description(description)
                .pointsCost(pointsCost)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .status(true)
                .isPublic(true)
                .maxRedemptions(100)
                .isGlobal(isGlobal)
                .benefitType(benefitType)
                .tierEntity(tier)
                .cafeEntity(cafe)
                .build());
    }
}