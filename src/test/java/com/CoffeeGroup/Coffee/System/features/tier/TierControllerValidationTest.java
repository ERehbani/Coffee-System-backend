package com.CoffeeGroup.Coffee.System.features.tier;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.GlobalExceptionHandler;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.CreateTierRequest;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponse;

/** Pins the request validation and error bodies the frontend consumes. Method security is not exercised here. */
@ExtendWith(MockitoExtension.class)
class TierControllerValidationTest {

    @Mock
    private ITierService tierService;

    private MockMvc mockMvc;

    private final UUID externalId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TierController(tierService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static String body(String name, Object minPoints) {
        String nameJson = name == null ? "null" : "\"" + name + "\"";
        return "{\"name\":" + nameJson + ",\"minPoints\":" + minPoints + "}";
    }

    @Test
    void postRejectsABlankName() throws Exception {
        mockMvc.perform(post("/tiers").contentType(MediaType.APPLICATION_JSON).content(body("   ", 100)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("The name is required"))
                .andExpect(jsonPath("$.status").value(400));
        verify(tierService, never()).create(any());
    }

    @Test
    void postRejectsANameLongerThanFiftyCharacters() throws Exception {
        mockMvc.perform(post("/tiers").contentType(MediaType.APPLICATION_JSON).content(body("a".repeat(51), 100)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("The name must have at most 50 characters"));
        verify(tierService, never()).create(any());
    }

    @Test
    void postRejectsNullMinPoints() throws Exception {
        mockMvc.perform(post("/tiers").contentType(MediaType.APPLICATION_JSON).content(body("Plata", null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.minPoints").value("The minimum points are required"));
        verify(tierService, never()).create(any());
    }

    @Test
    void postRejectsNegativeMinPoints() throws Exception {
        mockMvc.perform(post("/tiers").contentType(MediaType.APPLICATION_JSON).content(body("Plata", -1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.minPoints").value("The minimum points cannot be negative"));
        verify(tierService, never()).create(any());
    }

    @Test
    void putAppliesTheSameValidation() throws Exception {
        mockMvc.perform(put("/tiers/" + externalId).contentType(MediaType.APPLICATION_JSON).content(body("", -5)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("The name is required"))
                .andExpect(jsonPath("$.errors.minPoints").value("The minimum points cannot be negative"));
        verify(tierService, never()).update(any(), any());
    }

    @Test
    void postAcceptsZeroMinPointsAndServesTheTierJson() throws Exception {
        when(tierService.create(new CreateTierRequest("Plata", 0)))
                .thenReturn(new TierResponse(externalId, "Plata", 0));

        mockMvc.perform(post("/tiers").contentType(MediaType.APPLICATION_JSON).content(body("Plata", 0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value(externalId.toString()))
                .andExpect(jsonPath("$.name").value("Plata"))
                .andExpect(jsonPath("$.minPoints").value(0));
    }

    @Test
    void duplicateTiersAnswerConflictWithThePlainMessage() throws Exception {
        when(tierService.create(any())).thenThrow(new EntityAlreadyExistsException("A tier named Plata already exists"));

        mockMvc.perform(post("/tiers").contentType(MediaType.APPLICATION_JSON).content(body("Plata", 1000)))
                .andExpect(status().isConflict())
                .andExpect(content().string("A tier named Plata already exists"));
    }

    @Test
    void movingTheBaseTierAnswersBadRequestWithTheErrorBody() throws Exception {
        when(tierService.update(eq(externalId), any()))
                .thenThrow(new IllegalStateException("The base tier must keep 0 points"));

        mockMvc.perform(put("/tiers/" + externalId).contentType(MediaType.APPLICATION_JSON).content(body("Bronce", 500)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The base tier must keep 0 points"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getServesTheTierList() throws Exception {
        when(tierService.getAll()).thenReturn(List.of(new TierResponse(externalId, "Bronce", 0)));

        mockMvc.perform(get("/tiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].minPoints").value(0));
    }
}
