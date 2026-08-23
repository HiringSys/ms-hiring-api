package com.example.hiringsys.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TemporaryPasswordGeneratorTest {

    private final TemporaryPasswordGenerator generator = new TemporaryPasswordGenerator();

    @Test
    void geraSenhaForteComDezesseisCaracteres() {
        String password = generator.gerar();

        assertThat(password).hasSize(16);
        assertThat(password).containsPattern("[A-Z]");
        assertThat(password).containsPattern("[a-z]");
        assertThat(password).containsPattern("[0-9]");
        assertThat(password).containsPattern("[!@#$%*_-]");
    }

    @Test
    void geraSenhasDiferentes() {
        assertThat(generator.gerar()).isNotEqualTo(generator.gerar());
    }
}
