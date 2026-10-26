package com.CoffeeGroup.Coffee.System.features.benefit_format;

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
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.exceptions.GlobalExceptionHandler;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.BenefitFormatResponse;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.CreateBenefitFormatRequest;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.UpdateBenefitFormatRequest;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;

/** Pins the request validation and error bodies the frontend consumes. Method security is not exercised here. */
@ExtendWith(MockitoExtension.class)
class BenefitFormatControllerValidationTest {

    @Mock
    private IBenefitFormatService service;

    private MockMvc mockMvc;

    private final UUID externalId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BenefitFormatController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static String createBody(String code, String name, String description, Boolean active) {
        return "{\"code\":" + quote(code) + ",\"displayName\":" + quote(name)
                + ",\"description\":" + quote(description) + ",\"active\":" + active + "}";
    }

    private static String updateBody(String name, String description, Boolean active) {
        return "{\"displayName\":" + quote(name) + ",\"description\":" + quote(description) + ",\"active\":" + active + "}";
    }

    private static String quote(String value) {
        return value == null ? "null" : "\"" + value + "\"";
    }

    @Test
    void postRejectsAMissingFormat() throws Exception {
        mockMvc.perform(post("/benefit-formats").contentType(MediaType.APPLICATION_JSON)
                        .content(createBody(null, "2x1", "Dos por uno", true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").value("The format is required"))
                .andExpect(jsonPath("$.status").value(400));
        verify(service, never()).create(any());
    }

    @Test
    void postRejectsABlankName() throws Exception {
        mockMvc.perform(post("/benefit-formats").contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("DOS_POR_UNO", "   ", "Dos por uno", true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.displayName").value("The name is required"));
        verify(service, never()).create(any());
    }

    @Test
    void postRejectsANameLongerThanFortyCharacters() throws Exception {
        mockMvc.perform(post("/benefit-formats").contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("DOS_POR_UNO", "a".repeat(41), "Dos por uno", true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.displayName").value("The name must have at most 40 characters"));
    }

    @Test
    void postRejectsABlankDescription() throws Exception {
        mockMvc.perform(post("/benefit-formats").contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("DOS_POR_UNO", "2x1", "", true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.description").value("The description is required"));
    }

    @Test
    void postRejectsADescriptionLongerThanTwoHundredCharacters() throws Exception {
        mockMvc.perform(post("/benefit-formats").contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("DOS_POR_UNO", "2x1", "a".repeat(201), true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.description").value("The description must have at most 200 characters"));
    }

    @Test
    void putAppliesTheSameValidation() throws Exception {
        mockMvc.perform(put("/benefit-formats/" + externalId).contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody("", "a".repeat(201), true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.displayName").value("The name is required"))
                .andExpect(jsonPath("$.errors.description").value("The description must have at most 200 characters"));
        verify(service, never()).update(any(), any());
    }

    @Test
    void postServesTheCreatedFormatJson() throws Exception {
        when(service.create(new CreateBenefitFormatRequest(BenefitType.DOS_POR_UNO, "2x1", "Dos por uno", null)))
                .thenReturn(new BenefitFormatResponse(externalId, BenefitType.DOS_POR_UNO, "2x1", "Dos por uno", true, 0L));

        mockMvc.perform(post("/benefit-formats").contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("DOS_POR_UNO", "2x1", "Dos por uno", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value(externalId.toString()))
                .andExpect(jsonPath("$.code").value("DOS_POR_UNO"))
                .andExpect(jsonPath("$.displayName").value("2x1"))
                .andExpect(jsonPath("$.description").value("Dos por uno"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.usageCount").value(0));
    }

    @Test
    void duplicatesAnswerConflictWithThePlainMessage() throws Exception {
        when(service.create(any())).thenThrow(new EntityAlreadyExistsException("The format DOS_POR_UNO is already in the catalog"));

        mockMvc.perform(post("/benefit-formats").contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("DOS_POR_UNO", "2x1", "Dos por uno", true)))
                .andExpect(status().isConflict())
                .andExpect(content().string("The format DOS_POR_UNO is already in the catalog"));
    }

    @Test
    void anUnknownFormatOnPutAnswersNotFoundWithThePlainMessage() throws Exception {
        when(service.update(eq(externalId), any(UpdateBenefitFormatRequest.class)))
                .thenThrow(new EntityNotFoundException("Benefit format not found"));

        mockMvc.perform(put("/benefit-formats/" + externalId).contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody("2x1", "Dos por uno", true)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Benefit format not found"));
    }

    @Test
    void getServesTheCatalogWithUsageCounts() throws Exception {
        when(service.getAll()).thenReturn(List.of(
                new BenefitFormatResponse(externalId, BenefitType.CODIGO_DESCUENTO, "Descuento", "Reduce el precio", true, 7L)));

        mockMvc.perform(get("/benefit-formats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code").value("CODIGO_DESCUENTO"))
                .andExpect(jsonPath("$[0].usageCount").value(7));
    }
}
