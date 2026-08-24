package com.example.hiringsys.controller;

import com.example.hiringsys.entity.Grupo;
import com.example.hiringsys.enums.EstadoGrupo;
import com.example.hiringsys.service.GrupoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GrupoControllerTest {

    @Mock
    private GrupoService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new GrupoController(service))
                .build();
    }

    @Test
    void listaQuantidadeRealDeParticipantes() throws Exception {
        Grupo grupo = new Grupo();
        grupo.setId(1L);
        grupo.setNome("Backend Java");
        grupo.setArea("Tecnologia");
        grupo.setEstado(EstadoGrupo.EM_PROCESSO);
        grupo.setDisponiveis(5);
        grupo.setLimiteAprovados(3);
        grupo.setEmailEquipe("rh@hiringsys.local");

        when(service.listarTodos()).thenReturn(List.of(grupo));
        when(service.contarParticipantes(List.of(grupo))).thenReturn(Map.of(1L, 7L));

        mockMvc.perform(get("/grupos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantidadeParticipantes").value(7));
    }
}
