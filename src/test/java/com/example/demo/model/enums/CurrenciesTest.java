package com.example.demo.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CurrenciesTest {

    @Test
    @DisplayName("A pénznem enum a várt értékeket tartalmazza")
    void currenciesContainExpectedValues() {
        assertThat(Currencies.values()).containsExactly(Currencies.HUF, Currencies.EUR);
        assertThat(Currencies.valueOf("HUF")).isEqualTo(Currencies.HUF);
        assertThat(Currencies.valueOf("EUR")).isEqualTo(Currencies.EUR);
    }
}
