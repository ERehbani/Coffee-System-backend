package com.CoffeeGroup.Coffee.System.common.seed;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateAccountRequest;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateCafeAccountRequest;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitFormatRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.cafe.OpeningHours;
import com.CoffeeGroup.Coffee.System.features.catalog.CatalogProductEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.ICatalogProductService;
import com.CoffeeGroup.Coffee.System.features.catalog.domain.dto.CatalogProductRequest;
import com.CoffeeGroup.Coffee.System.features.category.CategoryRepository;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductRepository;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;
import com.CoffeeGroup.Coffee.System.features.user.IUserService;

import lombok.RequiredArgsConstructor;

@Component
@Order(2)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {

    static final String DEMO_PASSWORD_CAFE = "Cafe1234";
    static final String DEMO_CLIENT_EMAIL = "cliente@test.com";
    static final String DEMO_LOGO = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=200";
    static final String DEMO_COVER = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=1200";
    static final List<DayOfWeek> WEEKDAYS = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
    static final String DEMO_CLIENT_PASSWORD = "Cliente123";

    private final CredentialsRepository credentialsRepository;
    private final IUserService userService;
    private final CafeRepository cafeRepository;
    private final CategoryRepository categoryRepository;
    private final ICatalogProductService catalogProductService;
    private final CafeProductRepository cafeProductRepository;
    private final BenefitRepository benefitRepository;
    private final BenefitFormatRepository benefitFormatRepository;
    private final TierRepository tierRepository;
    private final Clock clock;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!credentialsRepository.existsByUsername(DEMO_CLIENT_EMAIL)) {
            userService.save(new CreateAccountRequest(DEMO_CLIENT_EMAIL, DEMO_CLIENT_PASSWORD, "Cliente Demo"));
        }
        if (credentialsRepository.existsByUsername("cafe.central@test.com")) {
            return;
        }
        CafeEntity central = createCafe("cafe.central@test.com", "Café Central", "Av Siempreviva 123",
                "Cafetería de prueba en el centro", "1111111111");
        completeProfile(central, openEvery(WEEKDAYS, LocalTime.of(8, 0), LocalTime.of(20, 0)));
        CafeEntity north = createCafe("cafe.norte@test.com", "Café Norte", "Calle Falsa 456",
                "Cafetería de prueba zona norte", "2222222222");
        completeProfile(north, openEvery(List.of(DayOfWeek.values()), LocalTime.of(9, 0), LocalTime.of(22, 0)));

        CafeProductEntity capuccino = sell(central, "Capuccino", "Cafe", "Capuccino clásico", "2500", 80);
        sell(central, "Medialuna", "Pasteleria", "Medialuna de manteca", "800", 50);
        sell(north, "Latte", "Cafe", "Café latte grande", "2800", 90);
        CafeProductEntity northMedialuna = sell(north, "Medialuna", "Pasteleria", "Medialuna de manteca", "900", 50);

        TierEntity base = tierRepository.findByMinPoints(0)
                .orElseThrow(() -> new EntityNotFoundException("Tier base no encontrado"));
        BenefitEntity welcome = benefit("Descuento 15% bienvenida", "Descuento de bienvenida para socios", 500,
                BenefitType.CODIGO_DESCUENTO, base, null);
        welcome.setDiscountPercentage(BigDecimal.valueOf(15));
        BenefitEntity capuccinoForPoints = benefit("Capuccino por puntos", "Cambia tus puntos por un capuccino", 300,
                BenefitType.PRODUCTO_POR_PUNTOS, base, central);
        capuccinoForPoints.setCafeProduct(capuccino);
        BenefitEntity twoForOne = benefit("2x1 Medialunas", "Lleva 2 medialunas y paga solo 1", 200,
                BenefitType.DOS_POR_UNO, base, null);
        twoForOne.setCatalogProduct(northMedialuna.getCatalogProduct());
        benefitRepository.save(welcome);
        benefitRepository.save(capuccinoForPoints);
        benefitRepository.save(twoForOne);
    }

    private CafeEntity createCafe(String email, String name, String address, String description, String phone) {
        var account = userService.saveCafe(new CreateCafeAccountRequest(email, DEMO_PASSWORD_CAFE, name, address,
                description, phone));
        return cafeRepository.findByExternalId(account.externalId())
                .orElseThrow(() -> new EntityNotFoundException("Cafetería de demostración no encontrada"));
    }

    private void completeProfile(CafeEntity cafe, List<OpeningHours> hours) {
        cafe.setLogo(DEMO_LOGO);
        cafe.setImage(DEMO_COVER);
        cafe.replaceOpeningHours(hours);
        cafeRepository.save(cafe);
    }

    private static List<OpeningHours> openEvery(List<DayOfWeek> days, LocalTime opensAt, LocalTime closesAt) {
        return days.stream().map(day -> OpeningHours.of(day, opensAt, closesAt)).toList();
    }

    private CafeProductEntity sell(CafeEntity cafe, String name, String category, String description, String price,
            int points) {
        CatalogProductEntity catalogProduct = catalogProductService.findOrCreate(new CatalogProductRequest(
                name, description, null, categoryRepository.findByName(category)
                        .orElseThrow(() -> new EntityNotFoundException("Categoría " + category + " no encontrada"))
                        .getExternalId()), cafe);
        return cafeProductRepository.save(CafeProductEntity.builder()
                .cafe(cafe)
                .catalogProduct(catalogProduct)
                .price(new BigDecimal(price))
                .awardedPoints(points)
                .build());
    }

    private BenefitEntity benefit(String name, String description, int pointsCost, BenefitType type, TierEntity tier,
            CafeEntity cafe) {
        LocalDate today = LocalDate.now(clock);
        return BenefitEntity.builder()
                .name(name)
                .description(description)
                .pointsCost(pointsCost)
                .startDate(today)
                .endDate(today.plusMonths(3))
                .active(true)
                .isPublic(true)
                .maxRedemptions(100)
                .format(benefitFormatRepository.findByCode(type)
                        .orElseThrow(() -> new EntityNotFoundException("Formato " + type + " no encontrado")))
                .tier(tier)
                .cafe(cafe)
                .build();
    }
}
