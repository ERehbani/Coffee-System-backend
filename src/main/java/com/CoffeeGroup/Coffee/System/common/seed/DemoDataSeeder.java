package com.CoffeeGroup.Coffee.System.common.seed;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;
import com.CoffeeGroup.Coffee.System.features.client.IClientTierService;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductRepository;
import com.CoffeeGroup.Coffee.System.features.product.ProductPointsPolicy;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.PointsRangeResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseRepository;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseStatus;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;
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
    static final String DEMO_CLIENT_PASSWORD = "Cliente123";
    static final String CENTRAL = "cafe.central@test.com";
    static final String NORTH = "cafe.norte@test.com";
    static final String SOUTH = "cafe.sur@test.com";
    static final String GRANO = "cafe.grano@test.com";
    static final String CORNER = "cafe.esquina@test.com";

    private static final String IMG = "https://images.unsplash.com/photo-";
    private static final List<DayOfWeek> WEEKDAYS = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
    private static final List<DayOfWeek> MONDAY_TO_SATURDAY = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);
    private static final List<DayOfWeek> EVERY_DAY = List.of(DayOfWeek.values());

    private enum Generosity { FLOOR, SUGGESTED, CAP }

    private record DemoProduct(String name, String category, String description, String price) {
    }

    private record DemoCafe(String email, String name, String address, String description, String phone,
            String logo, String cover, List<OpeningHours> hours, Generosity generosity, List<DemoProduct> products) {
    }

    private record DemoClient(String email, String name, int targetPoints, List<String> usualCafes) {
    }

    private static final List<DemoCafe> CAFES = List.of(
            new DemoCafe(CENTRAL, "Cafe Central", "Av Siempreviva 123", "Cafeteria de prueba en el centro",
                    "1111111111", IMG + "1509042239860-f550ce710b93?w=200", IMG + "1554118811-1e0d58224f24?w=1200",
                    openEvery(WEEKDAYS, LocalTime.of(8, 0), LocalTime.of(20, 0)), Generosity.SUGGESTED, List.of(
                            new DemoProduct("Capuccino", "Cafe", "Capuccino clasico", "2500"),
                            new DemoProduct("Medialuna", "Pasteleria", "Medialuna de manteca", "800"),
                            new DemoProduct("Espresso", "Cafe", "Espresso corto de especialidad", "1800"),
                            new DemoProduct("Tostado", "Sandwiches", "Tostado de jamon y queso", "4200"))),
            new DemoCafe(NORTH, "Cafe Norte", "Calle Falsa 456", "Cafeteria de prueba zona norte", "2222222222",
                    IMG + "1495474472287-4d71bcdd2085?w=200", IMG + "1501339847302-ac426a4a7cbb?w=1200",
                    openEvery(EVERY_DAY, LocalTime.of(9, 0), LocalTime.of(22, 0)), Generosity.SUGGESTED, List.of(
                            new DemoProduct("Latte", "Cafe", "Cafe latte grande", "2800"),
                            new DemoProduct("Medialuna", "Pasteleria", "Medialuna de manteca", "900"),
                            new DemoProduct("Capuccino", "Cafe", "Capuccino con canela", "2700"),
                            new DemoProduct("Te verde", "Tes e Infusiones", "Te verde en hebras", "1600"))),
            new DemoCafe(SOUTH, "Tostadero del Sur", "Av Belgrano 1850", "Tostamos nuestro propio cafe",
                    "3333333333", IMG + "1442512595331-e89e73853f31?w=200", IMG + "1445116572660-236099ec97a0?w=1200",
                    openEvery(MONDAY_TO_SATURDAY, LocalTime.of(7, 0), LocalTime.of(19, 0)), Generosity.CAP, List.of(
                            new DemoProduct("Capuccino", "Cafe", "Capuccino con leche de almendras", "3200"),
                            new DemoProduct("Flat White", "Cafe", "Doble ristretto con leche", "3400"),
                            new DemoProduct("Alfajor de maicena", "Pasteleria", "Alfajor casero", "1200"),
                            new DemoProduct("Medialuna", "Pasteleria", "Medialuna de manteca", "1000"))),
            new DemoCafe(GRANO, "Grano de Oro", "Thames 1420", "Cafe de especialidad y brunch", "4444444444",
                    IMG + "1447933601403-0c6688de566e?w=200", IMG + "1521017432531-fbd92d768814?w=1200",
                    openEvery(EVERY_DAY, LocalTime.of(10, 0), LocalTime.of(23, 0)), Generosity.SUGGESTED, List.of(
                            new DemoProduct("Espresso", "Cafe", "Espresso de origen Colombia", "2000"),
                            new DemoProduct("Capuccino", "Cafe", "Capuccino de especialidad", "2600"),
                            new DemoProduct("Cold Brew", "Bebidas Frias", "Cafe infusionado en frio", "3800"),
                            new DemoProduct("Budin de limon", "Pasteleria", "Porcion de budin de limon", "2200"))),
            new DemoCafe(CORNER, "La Esquina", "Corrientes 3200", "El cafe del barrio", "5555555555",
                    IMG + "1509042239860-f550ce710b93?w=200", IMG + "1554118811-1e0d58224f24?w=1200",
                    openEvery(WEEKDAYS, LocalTime.of(8, 0), LocalTime.of(16, 0)), Generosity.FLOOR, List.of(
                            new DemoProduct("Latte", "Cafe", "Latte con vainilla", "3000"),
                            new DemoProduct("Medialuna", "Pasteleria", "Medialuna de grasa", "850"),
                            new DemoProduct("Tostado", "Sandwiches", "Tostado de jamon y queso", "4500"),
                            new DemoProduct("Limonada", "Bebidas Frias", "Limonada con menta y jengibre", "2500"))));

    private static final List<DemoClient> CLIENTS = List.of(
            new DemoClient("valentina@test.com", "Valentina Lopez", 0, List.of()),
            new DemoClient("lucia@test.com", "Lucia Gomez", 450, List.of(CENTRAL)),
            new DemoClient("martin@test.com", "Martin Perez", 1500, List.of(CENTRAL, NORTH)),
            new DemoClient("sofia@test.com", "Sofia Fernandez", 3800, List.of(SOUTH, GRANO, CENTRAL)),
            new DemoClient("tomas@test.com", "Tomas Rodriguez", 7600, List.of(CENTRAL, NORTH, SOUTH, GRANO, CORNER)));

    private final CredentialsRepository credentialsRepository;
    private final IUserService userService;
    private final CafeRepository cafeRepository;
    private final ClientRepository clientRepository;
    private final CategoryRepository categoryRepository;
    private final ICatalogProductService catalogProductService;
    private final CafeProductRepository cafeProductRepository;
    private final ProductPointsPolicy pointsPolicy;
    private final PurchaseRepository purchaseRepository;
    private final IClientTierService clientTierService;
    private final BenefitRepository benefitRepository;
    private final BenefitFormatRepository benefitFormatRepository;
    private final TierRepository tierRepository;
    private final Clock clock;

    private final Map<String, CafeEntity> cafes = new LinkedHashMap<>();
    private final Map<String, List<CafeProductEntity>> menus = new HashMap<>();

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!credentialsRepository.existsByUsername(DEMO_CLIENT_EMAIL)) {
            userService.save(new CreateAccountRequest(DEMO_CLIENT_EMAIL, DEMO_CLIENT_PASSWORD, "Cliente Demo"));
        }
        for (DemoCafe spec : CAFES) {
            seedCafe(spec);
        }
        List<DemoClient> newClients = new ArrayList<>();
        for (DemoClient spec : CLIENTS) {
            if (!credentialsRepository.existsByUsername(spec.email())) {
                userService.save(new CreateAccountRequest(spec.email(), DEMO_CLIENT_PASSWORD, spec.name()));
                newClients.add(spec);
            }
        }
        Set<String> newEmails = newClients.stream().map(DemoClient::email).collect(Collectors.toSet());
        seedOpenOrders(newEmails);
        newClients.forEach(this::buyUntil);
    }

    private void seedCafe(DemoCafe spec) {
        if (credentialsRepository.existsByUsername(spec.email())) {
            cafes.put(spec.email(), cafeRepository.findByUser_Credentials_Username(spec.email())
                    .orElseThrow(() -> new EntityNotFoundException("Demo cafe " + spec.email() + " not found")));
            return;
        }
        userService.saveCafe(new CreateCafeAccountRequest(spec.email(), DEMO_PASSWORD_CAFE, spec.name(),
                spec.address(), spec.description(), spec.phone()));
        CafeEntity cafe = cafeRepository.findByUser_Credentials_Username(spec.email())
                .orElseThrow(() -> new EntityNotFoundException("Demo cafe " + spec.email() + " not found"));
        cafe.setLogo(spec.logo());
        cafe.setImage(spec.cover());
        cafe.replaceOpeningHours(spec.hours());
        cafeRepository.save(cafe);
        cafes.put(spec.email(), cafe);

        Map<String, CafeProductEntity> sold = new HashMap<>();
        for (DemoProduct product : spec.products()) {
            sold.put(product.name(), sell(cafe, product, spec.generosity()));
        }
        seedBenefitsOf(spec.email(), cafe, sold);
    }

    private CafeProductEntity sell(CafeEntity cafe, DemoProduct spec, Generosity generosity) {
        CatalogProductEntity catalogProduct = catalogProductService.findOrCreate(new CatalogProductRequest(
                spec.name(), spec.description(), null, categoryRepository.findByName(spec.category())
                        .orElseThrow(() -> new EntityNotFoundException("Category " + spec.category() + " not found"))
                        .getExternalId()), cafe);
        BigDecimal price = new BigDecimal(spec.price());
        PointsRangeResponse range = pointsPolicy.rangeFor(cafe, catalogProduct, price);
        int points = switch (generosity) {
            case FLOOR -> range.min();
            case SUGGESTED -> range.suggested();
            case CAP -> range.max();
        };
        return cafeProductRepository.save(CafeProductEntity.builder()
                .cafe(cafe)
                .catalogProduct(catalogProduct)
                .price(price)
                .awardedPoints(points)
                .build());
    }

    private void seedBenefitsOf(String cafeEmail, CafeEntity cafe, Map<String, CafeProductEntity> sold) {
        TierEntity bronze = tier("BRONCE");
        switch (cafeEmail) {
            case CENTRAL -> {
                BenefitEntity welcome = benefit("Descuento 15% bienvenida", "Descuento de bienvenida para socios",
                        500, BenefitType.CODIGO_DESCUENTO, bronze, null);
                welcome.setDiscountPercentage(BigDecimal.valueOf(15));
                BenefitEntity capuccino = benefit("Capuccino por puntos", "Cambia tus puntos por un capuccino", 300,
                        BenefitType.PRODUCTO_POR_PUNTOS, bronze, cafe);
                capuccino.setCafeProduct(sold.get("Capuccino"));
                benefitRepository.saveAll(List.of(welcome, capuccino));
            }
            case NORTH -> {
                BenefitEntity twoForOne = benefit("2x1 Medialunas", "Lleva 2 medialunas y paga solo 1", 200,
                        BenefitType.DOS_POR_UNO, bronze, null);
                twoForOne.setCatalogProduct(sold.get("Medialuna").getCatalogProduct());
                benefitRepository.save(twoForOne);
            }
            case SOUTH -> {
                BenefitEntity flatWhite = benefit("Flat White por puntos", "Cambia tus puntos por un flat white", 400,
                        BenefitType.PRODUCTO_POR_PUNTOS, bronze, cafe);
                flatWhite.setCafeProduct(sold.get("Flat White"));
                benefitRepository.save(flatWhite);
            }
            case GRANO -> {
                BenefitEntity gold = benefit("20% socios Oro", "Descuento exclusivo para socios Oro o superior", 800,
                        BenefitType.CODIGO_DESCUENTO, tier("ORO"), cafe);
                gold.setDiscountPercentage(BigDecimal.valueOf(20));
                benefitRepository.save(gold);
            }
            case CORNER -> {
                BenefitEntity silver = benefit("10% socios Plata", "Descuento para socios Plata o superior", 400,
                        BenefitType.CODIGO_DESCUENTO, tier("PLATA"), cafe);
                silver.setDiscountPercentage(BigDecimal.valueOf(10));
                benefitRepository.save(silver);
            }
            default -> {
            }
        }
    }

    private void seedOpenOrders(Set<String> newClients) {
        LocalDateTime now = LocalDateTime.now(clock);
        order(newClients, "lucia@test.com", CENTRAL, now.minusMinutes(4), PurchaseStatus.CONFIRMED, 2);
        order(newClients, "martin@test.com", CENTRAL, now.minusMinutes(11), PurchaseStatus.CONFIRMED, 1);
        order(newClients, "sofia@test.com", CENTRAL, now.minusMinutes(18), PurchaseStatus.IN_PREPARATION, 1);
        order(newClients, "tomas@test.com", CENTRAL, now.minusMinutes(32), PurchaseStatus.READY, 2);
        order(newClients, "martin@test.com", NORTH, now.minusMinutes(7), PurchaseStatus.CONFIRMED, 1);
        order(newClients, "tomas@test.com", GRANO, now.minusMinutes(15), PurchaseStatus.IN_PREPARATION, 1);
    }

    private void order(Set<String> newClients, String clientEmail, String cafeEmail, LocalDateTime date,
            PurchaseStatus status, int amount) {
        if (!newClients.contains(clientEmail)) {
            return;
        }
        ClientEntity client = client(clientEmail);
        List<CafeProductEntity> menu = menuOf(cafeEmail);
        if (menu.isEmpty()) {
            return;
        }
        purchase(client, cafes.get(cafeEmail), date, status, menu.subList(0, Math.min(2, menu.size())), amount);
    }

    private void buyUntil(DemoClient spec) {
        ClientEntity client = client(spec.email());
        LocalDateTime lastVisit = LocalDateTime.now(clock).minusDays(1).withHour(10).withMinute(30);
        int visit = 0;
        while (client.getHistoricalPoints() < spec.targetPoints() && !spec.usualCafes().isEmpty() && visit < 200) {
            String cafeEmail = spec.usualCafes().get(visit % spec.usualCafes().size());
            List<CafeProductEntity> menu = menuOf(cafeEmail);
            if (menu.isEmpty()) {
                break;
            }
            List<CafeProductEntity> items = List.of(menu.get(visit % menu.size()),
                    menu.get((visit + 1) % menu.size()));
            purchase(client, cafes.get(cafeEmail), lastVisit.minusHours(visit * 37L), PurchaseStatus.DELIVERED,
                    items, 1 + visit % 2);
            visit++;
        }
        clientTierService.upgradeTierIfReached(client);
        clientRepository.save(client);
    }

    private void purchase(ClientEntity client, CafeEntity cafe, LocalDateTime date, PurchaseStatus status,
            List<CafeProductEntity> products, int amount) {
        PurchaseEntity purchase = PurchaseEntity.builder()
                .date(date)
                .client(client)
                .cafe(cafe)
                .status(status)
                .price(BigDecimal.ZERO)
                .build();
        BigDecimal total = BigDecimal.ZERO;
        for (CafeProductEntity product : products) {
            purchase.addDetail(PurchaseDetailEntity.sold(product, amount));
            total = total.add(product.subtotalFor(amount));
        }
        purchase.setPrice(total);
        client.earnPoints(purchase.generatedPoints());
        purchaseRepository.save(purchase);
    }

    private List<CafeProductEntity> menuOf(String cafeEmail) {
        return menus.computeIfAbsent(cafeEmail,
                email -> cafeProductRepository.findByCafeAndActiveTrueOrderByIdAsc(cafes.get(email)));
    }

    private ClientEntity client(String email) {
        return clientRepository.findByUser_Credentials_Username(email)
                .orElseThrow(() -> new EntityNotFoundException("Demo client " + email + " not found"));
    }

    private TierEntity tier(String name) {
        return tierRepository.findByName(name)
                .or(() -> tierRepository.findByMinPoints(0))
                .orElseThrow(() -> new EntityNotFoundException("Tier " + name + " not found"));
    }

    private static List<OpeningHours> openEvery(List<DayOfWeek> days, LocalTime opensAt, LocalTime closesAt) {
        return days.stream().map(day -> OpeningHours.of(day, opensAt, closesAt)).toList();
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
                        .orElseThrow(() -> new EntityNotFoundException("Format " + type + " not found")))
                .tier(tier)
                .cafe(cafe)
                .build();
    }
}
