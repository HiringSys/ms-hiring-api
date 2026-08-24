package com.example.hiringsys.service;

import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.enums.StatusFuncionario;
import com.example.hiringsys.exception.InvalidStatusTransitionException;
import com.example.hiringsys.repository.CargoRepository;
import com.example.hiringsys.repository.FuncionarioRepository;
import com.example.hiringsys.repository.GrupoRepository;
import com.example.hiringsys.repository.RedeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock private FuncionarioRepository funcionarios;
    @Mock private CargoRepository cargos;
    @Mock private RedeRepository redes;
    @Mock private GrupoRepository grupos;
    @Mock private ArquivoFuncionarioService arquivos;

    private FuncionarioService service;

    @BeforeEach
    void setUp() {
        service = new FuncionarioService(funcionarios, cargos, redes, grupos, arquivos);
    }

    @Test
    void patchGenericoPermiteCorrigirStatusDiretamente() {
        Funcionario funcionario = funcionarioContratado();
        when(funcionarios.findById(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarios.save(funcionario)).thenReturn(funcionario);

        Funcionario atualizado = service.atualizarParcial(
                1L,
                Map.of("status", StatusFuncionario.EM_ANALISE)
        );

        assertThat(atualizado.getStatus()).isEqualTo(StatusFuncionario.EM_ANALISE);
        verify(funcionarios).save(funcionario);
    }

    @Test
    void endpointDeAvancoMantemRegraDeTransicao() {
        Funcionario funcionario = funcionarioContratado();
        when(funcionarios.findById(1L)).thenReturn(Optional.of(funcionario));

        assertThatThrownBy(() -> service.atualizarStatus(1L, StatusFuncionario.EM_ANALISE))
                .isInstanceOf(InvalidStatusTransitionException.class);

        verify(funcionarios, never()).save(funcionario);
    }

    private Funcionario funcionarioContratado() {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setStatus(StatusFuncionario.CONTRATADO);
        return funcionario;
    }
}
