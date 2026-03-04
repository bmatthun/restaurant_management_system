package com.example.demo.controller;

import com.example.demo.model.Bowl;
import com.example.demo.repository.BowlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BowlController.class)
class BowlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BowlRepository bowlRepository;

    @Test
    @DisplayName("GET /api/bowls - JSON válasz listával")
    void testGetAllBowls() throws Exception {
        Bowl bowl1 = new Bowl();
        bowl1.setId(1L);
        bowl1.setName("Klasszikus Buddha tál");
        bowl1.setQuantity(5);
        bowl1.setUnit("db");
        bowl1.setPrice(3500);

        Bowl bowl2 = new Bowl();
        bowl2.setId(2L);
        bowl2.setName("Vegán tál");
        bowl2.setQuantity(3);
        bowl2.setUnit("db");
        bowl2.setPrice(3200);

        List<Bowl> bowls = Arrays.asList(bowl1, bowl2);
        when(bowlRepository.findAll()).thenReturn(bowls);

        mockMvc.perform(get("/api/bowls"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Klasszikus Buddha tál"))
                .andExpect(jsonPath("$[0].quantity").value(5))
                .andExpect(jsonPath("$[0].unit").value("db"))
                .andExpect(jsonPath("$[0].price").value(3500))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Vegán tál"));
    }
}
