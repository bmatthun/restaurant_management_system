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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ThymeLeafController.class)
class ThymeLeafControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BowlRepository bowlRepository;

    @Test
    @DisplayName("GET /bowls - lista megjelenítése")
    void testListBowls() throws Exception {
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

        mockMvc.perform(get("/bowls"))
                .andExpect(status().isOk())
                .andExpect(view().name("bowls/list"))
                .andExpect(model().attributeExists("bowls"))
                .andExpect(model().attribute("bowls", bowls));
    }

    @Test
    @DisplayName("GET /bowls/new - űrlap megjelenítése")
    void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/bowls/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("bowls/form"))
                .andExpect(model().attributeExists("bowl"));
    }

    @Test
    @DisplayName("POST /bowls - új Bowl mentése")
    void testSaveBowl() throws Exception {
        Bowl savedBowl = new Bowl();
        savedBowl.setId(1L);
        savedBowl.setName("Klasszikus Buddha tál");
        savedBowl.setQuantity(5);
        savedBowl.setUnit("db");
        savedBowl.setPrice(3500);

        when(bowlRepository.save(any(Bowl.class))).thenReturn(savedBowl);

        mockMvc.perform(post("/bowls")
                        .param("name", "Klasszikus Buddha tál")
                        .param("quantity", "5")
                        .param("unit", "db")
                        .param("price", "3500"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bowls"));

        verify(bowlRepository).save(any(Bowl.class));
    }

    @Test
    @DisplayName("GET /bowls/delete/1 - Bowl törlése és redirect")
    void testDeleteBowl() throws Exception {
        Bowl bowl = new Bowl();
        bowl.setId(1L);
        bowl.setName("Klasszikus Buddha tál");

        when(bowlRepository.findById(1L)).thenReturn(java.util.Optional.of(bowl));
        doNothing().when(bowlRepository).delete(bowl);

        mockMvc.perform(get("/bowls/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bowls"));

        verify(bowlRepository).delete(bowl);
    }
}
