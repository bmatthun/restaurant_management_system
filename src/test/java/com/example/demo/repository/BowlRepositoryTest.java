package com.example.demo.repository;

import com.example.demo.model.Bowl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BowlRepositoryTest {

    @Autowired
    private BowlRepository bowlRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Bowl testBowl;

    @BeforeEach
    void setUp() {
        testBowl = new Bowl();
        testBowl.setName("Klasszikus Buddha tál");
        testBowl.setQuantity(5);
        testBowl.setUnit("db");
        testBowl.setPrice(3500);
    }

    @Test
    @DisplayName("Mentés - új Bowl mentése az adatbázisba")
    void testSave() {
        // When
        Bowl savedBowl = bowlRepository.save(testBowl);

        // Then
        assertThat(savedBowl).isNotNull();
        assertThat(savedBowl.getId()).isNotNull();
        assertThat(savedBowl.getName()).isEqualTo("Klasszikus Buddha tál");
        assertThat(savedBowl.getQuantity()).isEqualTo(5);
        assertThat(savedBowl.getUnit()).isEqualTo("db");
        assertThat(savedBowl.getPrice()).isEqualTo(3500);
    }

    @Test
    @DisplayName("Keresés ID alapján - létező Bowl keresése")
    void testFindById_Found() {
        // Given
        Bowl savedBowl = bowlRepository.save(testBowl);

        // When
        Optional<Bowl> foundBowl = bowlRepository.findById(savedBowl.getId());

        // Then
        assertThat(foundBowl).isPresent();
        assertThat(foundBowl.get().getId()).isEqualTo(savedBowl.getId());
        assertThat(foundBowl.get().getName()).isEqualTo("Klasszikus Buddha tál");
        assertThat(foundBowl.get().getPrice()).isEqualTo(3500);
    }

    @Test
    @DisplayName("Keresés ID alapján - nem létező Bowl")
    void testFindById_NotFound() {
        // When
        Optional<Bowl> foundBowl = bowlRepository.findById(999L);

        // Then
        assertThat(foundBowl).isEmpty();
    }

    @Test
    @DisplayName("Törlés - Bowl törlése az adatbázisból")
    void testDelete() {
        // Given
        Bowl savedBowl = bowlRepository.save(testBowl);
        Long savedId = savedBowl.getId();

        // When
        bowlRepository.delete(savedBowl);

        // Then
        Optional<Bowl> deletedBowl = bowlRepository.findById(savedId);
        assertThat(deletedBowl).isEmpty();
    }

    @Test
    @DisplayName("Módosítás - meglévő Bowl frissítése")
    void testUpdate() {
        // Given
        Bowl savedBowl = bowlRepository.save(testBowl);
        savedBowl.setName("Vegán Buddha tál");
        savedBowl.setPrice(3200);

        // When
        Bowl updatedBowl = bowlRepository.save(savedBowl);

        // Then
        assertThat(updatedBowl.getName()).isEqualTo("Vegán Buddha tál");
        assertThat(updatedBowl.getPrice()).isEqualTo(3200);
    }
}
