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

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ClientController(clientService, clientTierService)).build();
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
}
