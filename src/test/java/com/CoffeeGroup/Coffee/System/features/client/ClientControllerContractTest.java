package com.CoffeeGroup.Coffee.System.features.client;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
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
}
