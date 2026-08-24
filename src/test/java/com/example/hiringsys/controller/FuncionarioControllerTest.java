package com.example.hiringsys.controller;

import com.example.hiringsys.dto.response.FuncionarioIndicadoresResponse;
import com.example.hiringsys.enums.StatusFuncionario;
import com.example.hiringsys.mapper.FuncionarioMapper;
import com.example.hiringsys.service.FuncionarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FuncionarioControllerTest {

    @Mock private FuncionarioService service;
    @Mock private FuncionarioMapper mapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FuncionarioController(service, mapper))
                .build();
    }

    @Test
    void combinaFiltrosNaListagem() throws Exception {
        when(service.pesquisar("Ana", "Java", StatusFuncionario.EM_ANALISE))
                .thenReturn(List.of());

        mockMvc.perform(get("/funcionarios")
                        .param("nome", "Ana")
                        .param("cargo", "Java")
                        .param("status", "EM_ANALISE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(service).pesquisar("Ana", "Java", StatusFuncionario.EM_ANALISE);
    }

    @Test
    void retornaIndicadores() throws Exception {
        when(service.indicadores())
                .thenReturn(new FuncionarioIndicadoresResponse(10, 4, 2, 3, 1));

        mockMvc.perform(get("/funcionarios/indicadores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.emAnalise").value(4))
                .andExpect(jsonPath("$.aprovados").value(2))
                .andExpect(jsonPath("$.reprovados").value(3))
                .andExpect(jsonPath("$.contratados").value(1));
    }

    @Test
    void rejeitaCadastroSemCargo() throws Exception {
        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Ana Souza",
                                  "email": "ana@example.com",
                                  "cargoIds": []
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service, mapper);
    }
}
