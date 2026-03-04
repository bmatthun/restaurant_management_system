package com.example.demo;

import com.example.demo.model.Bowl;
import com.example.demo.repository.BowlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BowlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BowlRepository bowlRepository;

    @Test
    @DisplayName("Teljes flow: mentés, listázás, szerkesztés")
    void testFullBowlFlow() throws Exception {
        // 1. POST /bowls → új tál mentése
        mockMvc.perform(post("/bowls")
                        .param("name", "Integrációs Buddha tál")
                        .param("quantity", "10")
                        .param("unit", "db")
                        .param("price", "4500"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bowls"));

        // 2. GET /bowls → listázás és ellenőrzés
        mockMvc.perform(get("/bowls"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("bowls"));

        // Ellenőrizzük, hogy az adatbázisban is megvan
        Bowl savedBowl = bowlRepository.findAll().stream()
                .filter(b -> "Integrációs Buddha tál".equals(b.getName()))
                .findFirst()
                .orElseThrow();

        // 3. GET /bowls/edit/{id} → szerkesztés form betöltése
        mockMvc.perform(get("/bowls/edit/" + savedBowl.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("bowl"))
                .andExpect(model().attribute("bowl",
                        org.hamcrest.Matchers.hasProperty("name",
                                org.hamcrest.Matchers.equalTo("Integrációs Buddha tál"))));

        // Takarítás
        bowlRepository.delete(savedBowl);
    }
}
