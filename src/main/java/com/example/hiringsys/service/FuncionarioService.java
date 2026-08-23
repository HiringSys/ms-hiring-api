package com.example.hiringsys.service;

import com.example.hiringsys.dto.request.RedeRequest;
import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.entity.Rede;
import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusFuncionario;
import com.example.hiringsys.exception.BusinessRuleException;
import com.example.hiringsys.exception.InvalidStatusTransitionException;
import com.example.hiringsys.exception.ResourceNotFoundException;
import com.example.hiringsys.repository.CargoRepository;
import com.example.hiringsys.repository.FuncionarioRepository;
import com.example.hiringsys.repository.RedeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final CargoRepository cargoRepository;
    private final RedeRepository redeRepository;

    public FuncionarioService(FuncionarioRepository repository, CargoRepository cargoRepository,
                              RedeRepository redeRepository) {
        this.repository = repository;
        this.cargoRepository = cargoRepository;
        this.redeRepository = redeRepository;
    }

    @Transactional(readOnly = true)
    public List<Funcionario> listarTodos() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public Funcionario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Funcionario> buscarPorNome(String nome) { return repository.findByNomeContainingIgnoreCase(nome); }

    @Transactional(readOnly = true)
    public List<Funcionario> buscarPorStatus(StatusFuncionario status) { return repository.findByStatus(status); }

    @Transactional(readOnly = true)
    public List<Funcionario> buscarPorCargo(Long cargoId) { return repository.findByCargo(buscarCargo(cargoId)); }

    @Transactional
    public Funcionario salvar(Funcionario funcionario) {
        funcionario.setId(null);
        validarEmailNovo(funcionario.getEmail());
        validarSalario(funcionario.getSalario());
        funcionario.setCargos(resolverCargos(funcionario.getCargos()));
        funcionario.setRedes(resolverRedes(funcionario.getRedes()));
        funcionario.setStatus(StatusFuncionario.EM_ANALISE);
        if (funcionario.getExperiencia() == null) {
            funcionario.setExperiencia(ExperienciaFuncionario.SEM_EXPERIENCIA);
        }
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
        atualizarStatus(funcionario, dados.getStatus());
        funcionario.setExperiencia(dados.getExperiencia());
        funcionario.setCargos(resolverCargos(dados.getCargos()));
        funcionario.setRedes(resolverRedes(dados.getRedes()));
        return repository.save(funcionario);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Funcionario atualizarParcial(Long id, Map<String, Object> campos) {
        Funcionario funcionario = buscarPorId(id);
        if (campos.containsKey("nome")) funcionario.setNome((String) campos.get("nome"));
        if (campos.containsKey("telefone")) funcionario.setTelefone((String) campos.get("telefone"));
        if (campos.containsKey("cidade")) funcionario.setCidade((String) campos.get("cidade"));
        if (campos.containsKey("email")) {
            String email = (String) campos.get("email");
            validarEmailAtualizado(email, id);
            funcionario.setEmail(email);
        }
        if (campos.containsKey("salario")) {
            BigDecimal salario = (BigDecimal) campos.get("salario");
            validarSalario(salario);
            funcionario.setSalario(salario);
        }
        if (campos.containsKey("status")) atualizarStatus(funcionario, (StatusFuncionario) campos.get("status"));
        if (campos.containsKey("experiencia")) funcionario.setExperiencia((ExperienciaFuncionario) campos.get("experiencia"));
        if (campos.containsKey("cargoIds")) {
            Set<Cargo> cargos = new LinkedHashSet<>();
            ((Set<Long>) campos.get("cargoIds")).forEach(cargoId -> { Cargo cargo = new Cargo(); cargo.setId(cargoId); cargos.add(cargo); });
            funcionario.setCargos(resolverCargos(cargos));
        }
        if (campos.containsKey("redes")) {
            Set<Rede> redes = new LinkedHashSet<>();
            ((List<RedeRequest>) campos.get("redes")).forEach(item -> { Rede rede = new Rede(); rede.setTipo(item.tipo()); rede.setUrl(item.url()); redes.add(rede); });
            funcionario.setRedes(resolverRedes(redes));
        }
        return repository.save(funcionario);
    }

    @Transactional
    public void excluir(Long id) { repository.delete(buscarPorId(id)); }

    private Set<Cargo> resolverCargos(Set<Cargo> cargos) {
        Set<Cargo> resolvidos = new LinkedHashSet<>();
        if (cargos == null) return resolvidos;
        for (Cargo cargo : cargos) {
            if (cargo.getId() == null) throw new BusinessRuleException("O ID do cargo é obrigatório");
            resolvidos.add(buscarCargo(cargo.getId()));
        }
        return resolvidos;
    }

    private Set<Rede> resolverRedes(Set<Rede> redes) {
        Set<Rede> resolvidas = new LinkedHashSet<>();
        if (redes == null) return resolvidas;
        for (Rede rede : redes) {
            Rede existente = redeRepository.findByUrlIgnoreCase(rede.getUrl()).orElse(null);
            if (existente != null) {
                if (existente.getTipo() != rede.getTipo()) {
                    throw new BusinessRuleException("A URL já está cadastrada com outro tipo: " + rede.getUrl());
                }
                resolvidas.add(existente);
            } else {
                rede.setId(null);
                resolvidas.add(redeRepository.save(rede));
            }
        }
        return resolvidas;
    }

    private Cargo buscarCargo(Long id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado: " + id));
    }

    private void validarEmailNovo(String email) {
        if (repository.existsByEmailIgnoreCase(email)) throw new BusinessRuleException("E-mail já cadastrado: " + email);
    }

    private void validarEmailAtualizado(String email, Long id) {
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) throw new BusinessRuleException("E-mail já cadastrado: " + email);
    }

    private void validarSalario(BigDecimal salario) {
        if (salario != null && salario.signum() < 0) throw new BusinessRuleException("O salário não pode ser negativo");
    }

    private void atualizarStatus(Funcionario funcionario, StatusFuncionario novoStatus) {
        if (novoStatus == null || funcionario.getStatus() == novoStatus) return;
        StatusFuncionario atual = funcionario.getStatus();
        boolean permitida = switch (atual) {
            case EM_ANALISE -> novoStatus == StatusFuncionario.APROVADO || novoStatus == StatusFuncionario.REPROVADO;
            case APROVADO -> novoStatus == StatusFuncionario.CONTRATADO;
            case REPROVADO, CONTRATADO -> false;
        };
        if (!permitida) throw new InvalidStatusTransitionException(
                "Não é possível alterar o status de " + atual + " para " + novoStatus);
        funcionario.setStatus(novoStatus);
    }
}
