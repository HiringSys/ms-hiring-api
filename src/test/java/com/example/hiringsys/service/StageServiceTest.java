package com.example.hiringsys.service;

import com.example.hiringsys.dto.response.StageCandidateResponse;
import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.entity.Grupo;
import com.example.hiringsys.entity.GrupoFuncionario;
import com.example.hiringsys.enums.EstadoGrupo;
import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusSelecao;
import com.example.hiringsys.exception.BusinessRuleException;
import com.example.hiringsys.repository.CargoRepository;
import com.example.hiringsys.repository.FuncionarioRepository;
import com.example.hiringsys.repository.GrupoFuncionarioRepository;
import com.example.hiringsys.repository.GrupoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @Mock private GrupoRepository grupos;
    @Mock private GrupoFuncionarioRepository vinculos;
    @Mock private FuncionarioRepository funcionarios;
    @Mock private CargoRepository cargos;

    private StageService service;

    @BeforeEach
    void setUp() {
        service = new StageService(grupos, vinculos, funcionarios, cargos);
    }

    @Test
    void listarCandidatosPreservaScoreNaoCalculadoComoNulo() {
        Grupo grupo = grupo(1L, 2);
        GrupoFuncionario vinculo = vinculo(grupo, funcionario(10L, "Ana"));
        vinculo.setScoreProximidade(null);
        when(grupos.findById(1L)).thenReturn(Optional.of(grupo));
        when(vinculos.findByGrupoIdOrderByOrdemAprovacaoAsc(1L)).thenReturn(List.of(vinculo));

        List<StageCandidateResponse> response = service.listarCandidatos(1L);

        assertThat(response).singleElement().extracting(StageCandidateResponse::jobAffinity).isNull();
    }

    @Test
    void atualizarSelecaoRejeitaQuantidadeAcimaDoLimite() {
        Grupo grupo = grupo(1L, 1);
        when(grupos.findById(1L)).thenReturn(Optional.of(grupo));

        assertThatThrownBy(() -> service.atualizarSelecao(1L, List.of(10L, 11L)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("excede o limite");
        verify(vinculos, never()).saveAllAndFlush(anyList());
    }

    @Test
    void atualizarSelecaoPersisteStatusEOrdemDosAprovados() {
        Grupo grupo = grupo(1L, 2);
        GrupoFuncionario primeiro = vinculo(grupo, funcionario(10L, "Ana"));
        GrupoFuncionario segundo = vinculo(grupo, funcionario(11L, "Bia"));
        List<GrupoFuncionario> membros = List.of(primeiro, segundo);
        when(grupos.findById(1L)).thenReturn(Optional.of(grupo));
        when(vinculos.findByGrupoId(1L)).thenReturn(membros);
        when(vinculos.findByGrupoIdOrderByOrdemAprovacaoAsc(1L)).thenReturn(membros);

        service.atualizarSelecao(1L, List.of(11L, 10L));

        assertThat(segundo.getStatusSelecao()).isEqualTo(StatusSelecao.APROVADO);
        assertThat(segundo.getOrdemAprovacao()).isEqualTo(1);
        assertThat(primeiro.getStatusSelecao()).isEqualTo(StatusSelecao.APROVADO);
        assertThat(primeiro.getOrdemAprovacao()).isEqualTo(2);
        verify(vinculos, org.mockito.Mockito.times(2)).saveAllAndFlush(membros);
    }

    private Grupo grupo(Long id, int limite) {
        Grupo grupo = new Grupo();
        grupo.setId(id);
        grupo.setNome("Backend");
        grupo.setArea("Tecnologia");
        grupo.setEstado(EstadoGrupo.EM_PROCESSO);
        grupo.setDisponiveis(3);
        grupo.setCargo("Desenvolvedor");
        grupo.setLimiteAprovados(limite);
        grupo.setEmailEquipe("rh@example.com");
        return grupo;
    }

    private Funcionario funcionario(Long id, String nome) {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(id);
        funcionario.setNome(nome);
        funcionario.setEmail(nome.toLowerCase() + "@example.com");
        funcionario.setExperiencia(ExperienciaFuncionario.JUNIOR);
        funcionario.setAnosExperiencia(1);
        funcionario.setSalario(new BigDecimal("5000.00"));
        return funcionario;
    }

    private GrupoFuncionario vinculo(Grupo grupo, Funcionario funcionario) {
        GrupoFuncionario vinculo = new GrupoFuncionario();
        vinculo.setGrupo(grupo);
        vinculo.setFuncionario(funcionario);
        vinculo.setStatusSelecao(StatusSelecao.REPROVADO);
        return vinculo;
    }
}
