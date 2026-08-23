package com.example.hiringsys.service;

import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.enums.StatusFuncionario;
import com.example.hiringsys.exception.BusinessRuleException;
import com.example.hiringsys.exception.InvalidStatusTransitionException;
import com.example.hiringsys.exception.ResourceNotFoundException;
import com.example.hiringsys.repository.CargoRepository;
import com.example.hiringsys.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final CargoRepository cargoRepository;

    public FuncionarioService(FuncionarioRepository repository, CargoRepository cargoRepository) {
        this.repository = repository;
        this.cargoRepository = cargoRepository;
    }

    @Transactional(readOnly = true)
    public List<Funcionario> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Funcionario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Funcionario> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional(readOnly = true)
    public List<Funcionario> buscarPorStatus(StatusFuncionario status) {
        return repository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Funcionario> buscarPorCargo(Long cargoId) {
        return repository.findByCargo(buscarCargo(cargoId));
    }

    @Transactional
    public Funcionario salvar(Funcionario funcionario) {
        funcionario.setId(null);
        validarEmailNovo(funcionario.getEmail());
        validarSalario(funcionario.getSalario());
        funcionario.setCargo(validarCargo(funcionario.getCargo()));
        funcionario.setStatus(StatusFuncionario.EM_ANALISE);
        LocalDateTime agora = LocalDateTime.now();
        funcionario.setCriadoEm(agora);
        funcionario.setAtualizadoEm(agora);
        return repository.save(funcionario);
    }

    @Transactional
    public Funcionario atualizar(Long id, Funcionario dados) {
        Funcionario funcionario = buscarPorId(id);
        validarEmailAtualizado(dados.getEmail(), id);
        validarSalario(dados.getSalario());

        funcionario.setNome(dados.getNome());
        funcionario.setEmail(dados.getEmail());
        funcionario.setTelefone(dados.getTelefone());
        funcionario.setSalario(dados.getSalario());
        funcionario.setCidade(dados.getCidade());
        if (dados.getStatus() != null) {
            atualizarStatus(funcionario, dados.getStatus());
        }
        funcionario.setCargo(validarCargo(dados.getCargo()));
        funcionario.setAtualizadoEm(LocalDateTime.now());
        return repository.save(funcionario);
    }

    @Transactional
    public Funcionario atualizarParcial(Long id, Map<String, Object> campos) {
        Funcionario funcionario = buscarPorId(id);

        if (campos.containsKey("nome")) funcionario.setNome(valorTexto(campos.get("nome"), "nome"));
        if (campos.containsKey("telefone")) funcionario.setTelefone(valorTextoNulo(campos.get("telefone")));
        if (campos.containsKey("cidade")) funcionario.setCidade(valorTextoNulo(campos.get("cidade")));
        if (campos.containsKey("email")) {
            String email = valorTexto(campos.get("email"), "email");
            validarEmailAtualizado(email, id);
            funcionario.setEmail(email);
        }
        if (campos.containsKey("salario")) {
            BigDecimal salario = valorDecimal(campos.get("salario"));
            validarSalario(salario);
            funcionario.setSalario(salario);
        }
        if (campos.containsKey("status")) {
            atualizarStatus(funcionario, valorStatus(campos.get("status")));
        }
        if (campos.containsKey("cargo")) {
            funcionario.setCargo(buscarCargo(extrairCargoId(campos.get("cargo"))));
        }

        funcionario.setAtualizadoEm(LocalDateTime.now());
        return repository.save(funcionario);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarPorId(id));
    }

    private Cargo validarCargo(Cargo cargo) {
        if (cargo == null || cargo.getId() == null) {
            throw new BusinessRuleException("O cargo do funcionário é obrigatório");
        }
        return buscarCargo(cargo.getId());
    }

    private Cargo buscarCargo(Long id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado: " + id));
    }

    private void validarEmailNovo(String email) {
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new BusinessRuleException("E-mail já cadastrado: " + email);
        }
    }

    private void validarEmailAtualizado(String email, Long id) {
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new BusinessRuleException("E-mail já cadastrado: " + email);
        }
    }

    private void validarSalario(BigDecimal salario) {
        if (salario != null && salario.signum() < 0) {
            throw new BusinessRuleException("O salário não pode ser negativo");
        }
    }

    private String valorTexto(Object valor, String campo) {
        if (!(valor instanceof String texto) || texto.isBlank()) {
            throw new BusinessRuleException("O campo " + campo + " deve ser um texto não vazio");
        }
        return texto;
    }

    private String valorTextoNulo(Object valor) {
        if (valor == null) return null;
        if (valor instanceof String texto) return texto;
        throw new BusinessRuleException("O campo deve ser um texto ou nulo");
    }

    private BigDecimal valorDecimal(Object valor) {
        if (valor == null) return null;
        try {
            return new BigDecimal(valor.toString());
        } catch (NumberFormatException ex) {
            throw new BusinessRuleException("O salário deve ser um número válido");
        }
    }

    private StatusFuncionario valorStatus(Object valor) {
        if (valor == null) throw new BusinessRuleException("O status não pode ser nulo");
        try {
            return StatusFuncionario.valueOf(valor.toString());
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException("Status de funcionário inválido: " + valor);
        }
    }

    private void atualizarStatus(Funcionario funcionario, StatusFuncionario novoStatus) {
        StatusFuncionario statusAtual = funcionario.getStatus();

        if (statusAtual == novoStatus) {
            return;
        }

        boolean transicaoPermitida = switch (statusAtual) {
            case EM_ANALISE -> novoStatus == StatusFuncionario.APROVADO
                    || novoStatus == StatusFuncionario.REPROVADO;
            case APROVADO -> novoStatus == StatusFuncionario.CONTRATADO;
            case REPROVADO, CONTRATADO -> false;
        };

        if (!transicaoPermitida) {
            throw new InvalidStatusTransitionException(
                    "Não é possível alterar o status de " + statusAtual + " para " + novoStatus
            );
        }

        funcionario.setStatus(novoStatus);
    }

    private Long extrairCargoId(Object valor) {
        Object id = valor instanceof Map<?, ?> cargo ? cargo.get("id") : valor;
        if (id == null) throw new BusinessRuleException("O ID do cargo é obrigatório");
        try {
            return Long.valueOf(id.toString());
        } catch (NumberFormatException ex) {
            throw new BusinessRuleException("O ID do cargo deve ser numérico");
        }
    }
}
