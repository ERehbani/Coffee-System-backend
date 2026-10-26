package com.CoffeeGroup.Coffee.System.features.client;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierBenefitResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierLevelResponse;

/** Pins the JSON field names the frontend consumes. Method security is covered by ClientControllerSecurityTest. */
@ExtendWith(MockitoExtension.class)
class ClientControllerContractTest {

    @Mock
    private IClientService clientService;
    @Mock
    private IClientTierService clientTierService;
    @Mock
    private IClientDashboardService clientDashboardService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ClientController(clientService, clientTierService, clientDashboardService)).build();
    }

    @Test
    void getMyTierServesTheTierDetailUnderMeTier() throws Exception {
        when(clientTierService.viewMyTier()).thenReturn(new ClientTierResponse(
                "PLATA", 820, 1420, 1000, "ORO", 3000, 21, 1580,
                List.of(new TierLevelResponse("BRONCE", 0, List.of("Cafe de cortesia")))));

        mockMvc.perform(get("/clients/me/tier"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier").value("PLATA"))
                .andExpect(jsonPath("$.availablePoints").value(820))
                .andExpect(jsonPath("$.lifetimePoints").value(1420))
                .andExpect(jsonPath("$.currentTierThreshold").value(1000))
                .andExpect(jsonPath("$.nextTier").value("ORO"))
                .andExpect(jsonPath("$.nextTierThreshold").value(3000))
                .andExpect(jsonPath("$.progressPercent").value(21))
                .andExpect(jsonPath("$.pointsToNextTier").value(1580))
                .andExpect(jsonPath("$.levels[0].tier").value("BRONCE"))
                .andExpect(jsonPath("$.levels[0].threshold").value(0))
                .andExpect(jsonPath("$.levels[0].benefits[0]").value("Cafe de cortesia"));
    }

    @Test
    void getMyTierSerializesTheTopTierWithNullNextTier() throws Exception {
        when(clientTierService.viewMyTier()).thenReturn(new ClientTierResponse(
                "DIAMANTE", 100, 9000, 7000, null, null, 100, 0, List.of()));

        mockMvc.perform(get("/clients/me/tier"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"nextTier\":null")))
                .andExpect(content().string(containsString("\"nextTierThreshold\":null")))
                .andExpect(jsonPath("$.progressPercent").value(100))
                .andExpect(jsonPath("$.pointsToNextTier").value(0));
    }

    @Test
    void getMyBenefitsServesTheEnabledBenefitsUnderMeBenefits() throws Exception {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(clientTierService.viewMyBenefits()).thenReturn(List.of(
                new TierBenefitResponse(id, "Cafe de cortesia", "Un cafe gratis", 300,
                        LocalDate.of(2026, 12, 31), null, "BRONCE", "PRODUCTO_GRATIS_SOCIO")));

        mockMvc.perform(get("/clients/me/benefits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].externalId").value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$[0].name").value("Cafe de cortesia"))
                .andExpect(jsonPath("$[0].description").value("Un cafe gratis"))
                .andExpect(jsonPath("$[0].pointsCost").value(300))
                .andExpect(jsonPath("$[0].endDate").value("2026-12-31"))
                .andExpect(content().string(containsString("\"cafeName\":null")))
                .andExpect(jsonPath("$[0].tierName").value("BRONCE"))
                .andExpect(jsonPath("$[0].benefitType").value("PRODUCTO_GRATIS_SOCIO"));
    }

    @Test
    void getMyDashboardServesEverySectionWithTheFieldNamesTheFrontendExpects() throws Exception {
        UUID clientId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID redemptionId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID cafeId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        UUID benefitId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        when(clientDashboardService.viewMyDashboard()).thenReturn(new ClientDashboardResponse(
                new ClientDashboardResponse.Profile(clientId, "Ana", null, "ana@mail.com", null, "https://img/ana.png"),
                new ClientDashboardResponse.Loyalty("PLATA", 820, 1420, "ORO", 3000, 21, 1580),
                List.of(new ClientDashboardResponse.ActiveRedemption(
                        redemptionId, "Free coffee", "A free coffee", "ABC123", LocalDate.of(2026, 12, 31),
                        "Cafe Central")),
                List.of(new ClientDashboardResponse.NearbyCafe(
                        cafeId, "Cafe Central", null, 4.3, 12, false, null, 80, "Cafe en el centro")),
                List.of(new ClientDashboardResponse.FeaturedBenefit(
                        benefitId, "Free coffee", "A free coffee", null, 300))));

        mockMvc.perform(get("/clients/me/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client.id").value("22222222-2222-2222-2222-222222222222"))
                .andExpect(jsonPath("$.client.firstName").value("Ana"))
                .andExpect(jsonPath("$.client.lastName").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.client.email").value("ana@mail.com"))
                .andExpect(jsonPath("$.client.phoneNumber").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.client.avatarUrl").value("https://img/ana.png"))
                .andExpect(jsonPath("$.loyalty.tier").value("PLATA"))
                .andExpect(jsonPath("$.loyalty.availablePoints").value(820))
                .andExpect(jsonPath("$.loyalty.lifetimePoints").value(1420))
                .andExpect(jsonPath("$.loyalty.nextTier").value("ORO"))
                .andExpect(jsonPath("$.loyalty.nextTierThreshold").value(3000))
                .andExpect(jsonPath("$.loyalty.progressPercent").value(21))
                .andExpect(jsonPath("$.loyalty.pointsToNextTier").value(1580))
                .andExpect(jsonPath("$.activeRedemptions[0].id").value("33333333-3333-3333-3333-333333333333"))
                .andExpect(jsonPath("$.activeRedemptions[0].title").value("Free coffee"))
                .andExpect(jsonPath("$.activeRedemptions[0].description").value("A free coffee"))
                .andExpect(jsonPath("$.activeRedemptions[0].code").value("ABC123"))
                .andExpect(jsonPath("$.activeRedemptions[0].expiresAt").value("2026-12-31"))
                .andExpect(jsonPath("$.activeRedemptions[0].cafeName").value("Cafe Central"))
                .andExpect(jsonPath("$.nearbyCafes[0].id").value("44444444-4444-4444-4444-444444444444"))
                .andExpect(jsonPath("$.nearbyCafes[0].name").value("Cafe Central"))
                .andExpect(content().string(containsString("\"imageUrl\":null")))
                .andExpect(jsonPath("$.nearbyCafes[0].rating").value(4.3))
                .andExpect(jsonPath("$.nearbyCafes[0].reviewCount").value(12))
                .andExpect(jsonPath("$.nearbyCafes[0].isOpenNow").value(false))
                .andExpect(content().string(containsString("\"distanceMeters\":null")))
                .andExpect(jsonPath("$.nearbyCafes[0].maxPointsPerProduct").value(80))
                .andExpect(jsonPath("$.nearbyCafes[0].tagline").value("Cafe en el centro"))
                .andExpect(jsonPath("$.featuredBenefits[0].id").value("55555555-5555-5555-5555-555555555555"))
                .andExpect(jsonPath("$.featuredBenefits[0].title").value("Free coffee"))
                .andExpect(jsonPath("$.featuredBenefits[0].description").value("A free coffee"))
                .andExpect(jsonPath("$.featuredBenefits[0].pointsCost").value(300));
    }

    @Test
    void getMyDashboardSerializesAGlobalCouponWithNullCafeNameAndNoExpiry() throws Exception {
        when(clientDashboardService.viewMyDashboard()).thenReturn(new ClientDashboardResponse(
                new ClientDashboardResponse.Profile(UUID.randomUUID(), "Ana", null, null, null, null),
                new ClientDashboardResponse.Loyalty("BRONCE", 0, 0, "PLATA", 1000, 0, 1000),
                List.of(new ClientDashboardResponse.ActiveRedemption(
                        UUID.randomUUID(), "Global perk", "", "XYZ", null, null)),
                List.of(), List.of()));

        mockMvc.perform(get("/clients/me/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"expiresAt\":null")))
                .andExpect(content().string(containsString("\"cafeName\":null")));
    }

    @Test
    void getMyDashboardSerializesEmptySectionsAsEmptyArrays() throws Exception {
        when(clientDashboardService.viewMyDashboard()).thenReturn(new ClientDashboardResponse(
                new ClientDashboardResponse.Profile(UUID.randomUUID(), "Ana", null, null, null, null),
                new ClientDashboardResponse.Loyalty("BRONCE", 0, 0, "PLATA", 1000, 0, 1000),
                List.of(), List.of(), List.of()));

        mockMvc.perform(get("/clients/me/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeRedemptions").isEmpty())
                .andExpect(jsonPath("$.nearbyCafes").isEmpty())
                .andExpect(jsonPath("$.featuredBenefits").isEmpty());
    }
}
