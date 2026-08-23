package com.example.hiringsys.service;

import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.entity.Grupo;
import com.example.hiringsys.entity.Rede;
import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusFuncionario;
import com.example.hiringsys.exception.BusinessRuleException;
import com.example.hiringsys.exception.InvalidStatusTransitionException;
import com.example.hiringsys.exception.ResourceNotFoundException;
import com.example.hiringsys.repository.CargoRepository;
import com.example.hiringsys.repository.FuncionarioRepository;
import com.example.hiringsys.repository.GrupoRepository;
import com.example.hiringsys.repository.RedeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final CargoRepository cargoRepository;
    private final GrupoRepository grupoRepository;
    private final RedeRepository redeRepository;

    public FuncionarioService(
            FuncionarioRepository repository,
            CargoRepository cargoRepository,
            GrupoRepository grupoRepository,
            RedeRepository redeRepository
    ) {
        this.repository = repository;
        this.cargoRepository = cargoRepository;
        this.grupoRepository = grupoRepository;
        this.redeRepository = redeRepository;
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
    public List<Funcionario> buscarPorExperiencia(ExperienciaFuncionario experiencia) {
        return repository.findByExperiencia(experiencia);
    }

    @Transactional(readOnly = true)
    public List<Funcionario> buscarPorCargo(Long cargoId) {
        return repository.findByCargo(buscarCargo(cargoId));
    }

    @Transactional(readOnly = true)
    public List<Funcionario> buscarPorGrupo(Long grupoId) {
        buscarGrupo(grupoId);
        return repository.findByGrupoId(grupoId);
    }

    @Transactional
    public Funcionario salvar(Funcionario funcionario) {
        funcionario.setId(null);
        validarEmailNovo(funcionario.getEmail());
        validarSalario(funcionario.getSalario());
        funcionario.setCargos(resolverCargos(funcionario.getCargos()));
        funcionario.setGrupos(resolverGrupos(funcionario.getGrupos()));
        funcionario.setRedes(resolverRedes(funcionario.getRedes()));
        funcionario.setStatus(StatusFuncionario.EM_ANALISE);

        if (funcionario.getExperiencia() == null) {
            funcionario.setExperiencia(ExperienciaFuncionario.SEM_EXPERIENCIA);
        }

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
        funcionario.setExperiencia(dados.getExperiencia());
        funcionario.getCargos().clear();
        funcionario.getCargos().addAll(resolverCargos(dados.getCargos()));
        funcionario.getGrupos().clear();
        funcionario.getGrupos().addAll(resolverGrupos(dados.getGrupos()));
        funcionario.getRedes().clear();
        funcionario.getRedes().addAll(resolverRedes(dados.getRedes()));

        if (dados.getStatus() != null) {
            atualizarStatus(funcionario, dados.getStatus());
        }

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

        if (campos.containsKey("experiencia")) {
            funcionario.setExperiencia(valorExperiencia(campos.get("experiencia")));
        }

        if (campos.containsKey("cargoIds")) {
            Set<Cargo> cargos = resolverCargosPorIds(extrairIds(campos.get("cargoIds"), "cargoIds"));
            funcionario.getCargos().clear();
            funcionario.getCargos().addAll(cargos);
        }

        if (campos.containsKey("grupoIds")) {
            Set<Grupo> grupos = resolverGruposPorIds(extrairIds(campos.get("grupoIds"), "grupoIds"));
            funcionario.getGrupos().clear();
            funcionario.getGrupos().addAll(grupos);
        }

        if (campos.containsKey("redeIds")) {
            Set<Rede> redes = resolverRedesPorIds(extrairIds(campos.get("redeIds"), "redeIds"));
            funcionario.getRedes().clear();
            funcionario.getRedes().addAll(redes);
        }

        funcionario.setAtualizadoEm(LocalDateTime.now());
        return repository.save(funcionario);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarPorId(id));
    }

    private Set<Cargo> resolverCargos(Set<Cargo> cargos) {
        Set<Long> ids = new LinkedHashSet<>();
        if (cargos != null) {
            for (Cargo cargo : cargos) {
                if (cargo == null || cargo.getId() == null) {
                    throw new BusinessRuleException("Os IDs dos cargos são obrigatórios");
                }
                ids.add(cargo.getId());
            }
        }
        return resolverCargosPorIds(ids);
    }

    private Set<Cargo> resolverCargosPorIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessRuleException("Ao menos um cargo é obrigatório");
        }

        Set<Cargo> cargos = new LinkedHashSet<>();
        for (Long id : ids) cargos.add(buscarCargo(id));
        return cargos;
    }

    private Set<Grupo> resolverGrupos(Set<Grupo> grupos) {
        Set<Long> ids = new LinkedHashSet<>();
        if (grupos != null) {
            for (Grupo grupo : grupos) {
                if (grupo == null || grupo.getId() == null) {
                    throw new BusinessRuleException("Os IDs dos grupos devem ser informados");
                }
                ids.add(grupo.getId());
            }
        }
        return resolverGruposPorIds(ids);
    }

    private Set<Grupo> resolverGruposPorIds(Set<Long> ids) {
        Set<Grupo> grupos = new LinkedHashSet<>();
        if (ids != null) {
            for (Long id : ids) grupos.add(buscarGrupo(id));
        }
        return grupos;
    }

    private Set<Rede> resolverRedes(Set<Rede> redes) {
        Set<Long> ids = new LinkedHashSet<>();
        if (redes != null) {
            for (Rede rede : redes) {
                if (rede == null || rede.getId() == null) {
                    throw new BusinessRuleException("Os IDs das redes devem ser informados");
                }
                ids.add(rede.getId());
            }
        }
        return resolverRedesPorIds(ids);
    }

    private Set<Rede> resolverRedesPorIds(Set<Long> ids) {
        Set<Rede> redes = new LinkedHashSet<>();
        if (ids != null) {
            for (Long id : ids) redes.add(buscarRede(id));
        }
        return redes;
    }

    private Cargo buscarCargo(Long id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado: " + id));
    }

    private Grupo buscarGrupo(Long id) {
        return grupoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo não encontrado: " + id));
    }

    private Rede buscarRede(Long id) {
        return redeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rede não encontrada: " + id));
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
        } catch (NumberFormatException exception) {
            throw new BusinessRuleException("O salário deve ser um número válido");
        }
    }

    private StatusFuncionario valorStatus(Object valor) {
        if (valor == null) throw new BusinessRuleException("O status não pode ser nulo");
        try {
            return StatusFuncionario.valueOf(valor.toString());
        } catch (IllegalArgumentException exception) {
            throw new BusinessRuleException("Status de funcionário inválido: " + valor);
        }
    }

    private ExperienciaFuncionario valorExperiencia(Object valor) {
        if (valor == null) throw new BusinessRuleException("A experiência não pode ser nula");
        try {
            return ExperienciaFuncionario.valueOf(valor.toString());
        } catch (IllegalArgumentException exception) {
            throw new BusinessRuleException("Experiência de funcionário inválida: " + valor);
        }
    }

    private Set<Long> extrairIds(Object valor, String campo) {
        if (!(valor instanceof Iterable<?> valores)) {
            throw new BusinessRuleException("O campo " + campo + " deve ser uma lista de IDs");
        }

        Set<Long> ids = new LinkedHashSet<>();
        for (Object item : valores) {
            try {
                ids.add(Long.valueOf(item.toString()));
            } catch (NullPointerException | NumberFormatException exception) {
                throw new BusinessRuleException("O campo " + campo + " deve conter apenas IDs numéricos");
            }
        }
        return ids;
    }

    private void atualizarStatus(Funcionario funcionario, StatusFuncionario novoStatus) {
        StatusFuncionario statusAtual = funcionario.getStatus();

        if (statusAtual == novoStatus) return;

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
}
