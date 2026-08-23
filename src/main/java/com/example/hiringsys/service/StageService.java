package com.example.hiringsys.service;

import com.example.hiringsys.dto.request.StageCandidateRequest;
import com.example.hiringsys.dto.request.StageUpdateRequest;
import com.example.hiringsys.dto.response.StageCandidateResponse;
import com.example.hiringsys.dto.response.StageResponse;
import com.example.hiringsys.dto.response.StageSocialLinkResponse;
import com.example.hiringsys.entity.ArquivoFuncionario;
import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.entity.Funcionario;
import com.example.hiringsys.entity.Grupo;
import com.example.hiringsys.entity.GrupoFuncionario;
import com.example.hiringsys.enums.CategoriaArquivo;
import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusFuncionario;
import com.example.hiringsys.enums.StatusSelecao;
import com.example.hiringsys.enums.TipoRede;
import com.example.hiringsys.exception.BusinessRuleException;
import com.example.hiringsys.exception.ResourceNotFoundException;
import com.example.hiringsys.repository.CargoRepository;
import com.example.hiringsys.repository.FuncionarioRepository;
import com.example.hiringsys.repository.GrupoFuncionarioRepository;
import com.example.hiringsys.repository.GrupoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class StageService {

    private static final Set<TipoRede> FRONTEND_NETWORKS = Set.of(
            TipoRede.LINKEDIN, TipoRede.GITHUB, TipoRede.INSTAGRAM, TipoRede.FACEBOOK,
            TipoRede.X, TipoRede.WHATSAPP, TipoRede.GITLAB, TipoRede.BEHANCE,
            TipoRede.DRIBBBLE, TipoRede.TIKTOK
    );

    private final GrupoRepository grupos;
    private final GrupoFuncionarioRepository vinculos;
    private final FuncionarioRepository funcionarios;
    private final CargoRepository cargos;

    public StageService(
            GrupoRepository grupos,
            GrupoFuncionarioRepository vinculos,
            FuncionarioRepository funcionarios,
            CargoRepository cargos
    ) {
        this.grupos = grupos;
        this.vinculos = vinculos;
        this.funcionarios = funcionarios;
        this.cargos = cargos;
    }

    @Transactional(readOnly = true)
    public List<StageResponse> listar() {
        return grupos.findAll().stream().map(this::toStageResponse).toList();
    }

    @Transactional(readOnly = true)
    public StageResponse buscar(Long stageId) {
        return toStageResponse(buscarGrupo(stageId));
    }

    @Transactional
    public StageResponse atualizar(Long stageId, StageUpdateRequest request) {
        Grupo grupo = buscarGrupo(stageId);
        if (grupos.existsByNomeIgnoreCaseAndIdNot(request.jobTitle(), stageId)) {
            throw new BusinessRuleException("Ja existe uma peneira com esta finalidade: " + request.jobTitle());
        }
        if (request.approvalLimit() < quantidadeAprovados(stageId)) {
            throw new BusinessRuleException("O limite nao pode ser menor que a quantidade atual de aprovados");
        }
        grupo.setNome(request.jobTitle());
        grupo.setArea(request.department());
        grupo.setDisponiveis(request.availableSlots());
        grupo.setLimiteAprovados(request.approvalLimit());
        grupo.setEmailEquipe(request.teamEmail());
        return toStageResponse(grupos.save(grupo));
    }

    @Transactional
    public void excluir(Long stageId) {
        grupos.delete(buscarGrupo(stageId));
    }

    @Transactional(readOnly = true)
    public List<StageCandidateResponse> listarCandidatos(Long stageId) {
        buscarGrupo(stageId);
        return vinculos.findByGrupoIdOrderByOrdemAprovacaoAsc(stageId).stream()
                .map(this::toCandidateResponse)
                .toList();
    }

    @Transactional
    public StageCandidateResponse adicionarCandidato(Long stageId, StageCandidateRequest request) {
        Grupo grupo = buscarGrupo(stageId);
        if (funcionarios.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessRuleException("Ja existe um funcionario com o e-mail " + request.email());
        }

        Funcionario funcionario = new Funcionario();
        aplicarDadosCandidato(funcionario, request);
        funcionario.setStatus(StatusFuncionario.EM_ANALISE);
        funcionario = funcionarios.save(funcionario);

        GrupoFuncionario vinculo = new GrupoFuncionario();
        vinculo.setGrupo(grupo);
        vinculo.setFuncionario(funcionario);
        vinculo.setStatusSelecao(StatusSelecao.REPROVADO);
        vinculo.setOrdemAprovacao(null);
        vinculo.setScoreProximidade(null);
        return toCandidateResponse(vinculos.save(vinculo));
    }

    @Transactional
    public StageCandidateResponse atualizarCandidato(
            Long stageId,
            Long candidateId,
            StageCandidateRequest request
    ) {
        GrupoFuncionario vinculo = buscarVinculo(stageId, candidateId);
        if (funcionarios.existsByEmailIgnoreCaseAndIdNot(request.email(), candidateId)) {
            throw new BusinessRuleException("Ja existe um funcionario com o e-mail " + request.email());
        }
        aplicarDadosCandidato(vinculo.getFuncionario(), request);
        funcionarios.save(vinculo.getFuncionario());
        return toCandidateResponse(vinculo);
    }

    @Transactional
    public void removerCandidato(Long stageId, Long candidateId) {
        vinculos.delete(buscarVinculo(stageId, candidateId));
    }

    @Transactional
    public List<StageCandidateResponse> atualizarSelecao(Long stageId, List<Long> approvedCandidateIds) {
        Grupo grupo = buscarGrupo(stageId);
        Set<Long> idsUnicos = new HashSet<>(approvedCandidateIds);
        if (idsUnicos.size() != approvedCandidateIds.size()) {
            throw new BusinessRuleException("A lista de aprovados possui candidatos duplicados");
        }
        if (approvedCandidateIds.size() > grupo.getLimiteAprovados()) {
            throw new BusinessRuleException("A quantidade de aprovados excede o limite da peneira");
        }

        List<GrupoFuncionario> todos = vinculos.findByGrupoId(stageId);
        Set<Long> candidatosDoGrupo = new HashSet<>();
        todos.forEach(vinculo -> candidatosDoGrupo.add(vinculo.getFuncionario().getId()));
        if (!candidatosDoGrupo.containsAll(idsUnicos)) {
            throw new BusinessRuleException("A lista possui candidato que nao pertence a esta peneira");
        }

        todos.forEach(vinculo -> {
            vinculo.setStatusSelecao(StatusSelecao.REPROVADO);
            vinculo.setOrdemAprovacao(null);
        });
        vinculos.saveAllAndFlush(todos);

        for (int index = 0; index < approvedCandidateIds.size(); index++) {
            Long candidateId = approvedCandidateIds.get(index);
            GrupoFuncionario aprovado = todos.stream()
                    .filter(vinculo -> vinculo.getFuncionario().getId().equals(candidateId))
                    .findFirst()
                    .orElseThrow();
            aprovado.setStatusSelecao(StatusSelecao.APROVADO);
            aprovado.setOrdemAprovacao(index + 1);
        }
        vinculos.saveAllAndFlush(todos);
        return vinculos.findByGrupoIdOrderByOrdemAprovacaoAsc(stageId).stream()
                .map(this::toCandidateResponse)
                .toList();
    }

    private void aplicarDadosCandidato(Funcionario funcionario, StageCandidateRequest request) {
        funcionario.setNome(request.name());
        funcionario.setEmail(request.email());
        funcionario.setTelefone(request.phone());
        funcionario.setSalario(request.salaryExpectation());
        funcionario.setExperiencia(parseSeniority(request.seniority()));
        funcionario.setAnosExperiencia(request.experienceYears());
        funcionario.setCargos(new LinkedHashSet<>(Set.of(resolverCargo(request.role()))));
    }

    private Cargo resolverCargo(String nome) {
        return cargos.findByNomeIgnoreCase(nome).orElseGet(() -> {
            Cargo cargo = new Cargo();
            cargo.setNome(nome);
            return cargos.save(cargo);
        });
    }

    private ExperienciaFuncionario parseSeniority(String seniority) {
        return switch (seniority.toLowerCase(Locale.ROOT)) {
            case "junior" -> ExperienciaFuncionario.JUNIOR;
            case "pleno" -> ExperienciaFuncionario.PLENO;
            case "senior" -> ExperienciaFuncionario.SENIOR;
            default -> throw new BusinessRuleException("Senioridade invalida: " + seniority);
        };
    }

    private StageResponse toStageResponse(Grupo grupo) {
        return new StageResponse(
                grupo.getId(),
                grupo.getNome(),
                grupo.getArea(),
                grupo.getEstado().name().toLowerCase(Locale.ROOT),
                grupo.getDisponiveis(),
                vinculos.countByGrupoId(grupo.getId()),
                grupo.getCargo(),
                grupo.getLimiteAprovados(),
                grupo.getEmailEquipe()
        );
    }

    private StageCandidateResponse toCandidateResponse(GrupoFuncionario vinculo) {
        Funcionario funcionario = vinculo.getFuncionario();
        String role = funcionario.getCargos().stream().findFirst().map(Cargo::getNome)
                .orElse(vinculo.getGrupo().getCargo());
        List<StageSocialLinkResponse> networks = funcionario.getRedes().stream()
                .filter(rede -> FRONTEND_NETWORKS.contains(rede.getTipo()))
                .map(rede -> new StageSocialLinkResponse(
                        rede.getTipo().name().toLowerCase(Locale.ROOT), rede.getUrl()))
                .toList();
        return new StageCandidateResponse(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getEmail(),
                arquivoUrl(funcionario, CategoriaArquivo.FOTO),
                vinculo.getStatusSelecao().name().toLowerCase(Locale.ROOT),
                funcionario.getTelefone() == null ? "" : funcionario.getTelefone(),
                networks,
                toSeniority(funcionario.getExperiencia()),
                funcionario.getAnosExperiencia(),
                role,
                funcionario.getSalario() == null ? BigDecimal.ZERO : funcionario.getSalario(),
                arquivoUrl(funcionario, CategoriaArquivo.CURRICULO),
                vinculo.getScoreProximidade()
        );
    }

    private String arquivoUrl(Funcionario funcionario, CategoriaArquivo categoria) {
        return funcionario.getArquivos().stream()
                .filter(arquivo -> arquivo.getCategoria() == categoria)
                .findFirst()
                .map(arquivo -> downloadPath(funcionario, arquivo))
                .orElse(null);
    }

    private String downloadPath(Funcionario funcionario, ArquivoFuncionario arquivo) {
        return "/funcionarios/" + funcionario.getId() + "/arquivos/" + arquivo.getId() + "/download";
    }

    private String toSeniority(ExperienciaFuncionario experiencia) {
        return switch (experiencia) {
            case PLENO -> "pleno";
            case SENIOR -> "senior";
            case SEM_EXPERIENCIA, ESTAGIARIO, JUNIOR -> "junior";
        };
    }

    private long quantidadeAprovados(Long stageId) {
        return vinculos.findByGrupoId(stageId).stream()
                .filter(vinculo -> vinculo.getStatusSelecao() == StatusSelecao.APROVADO)
                .count();
    }

    private Grupo buscarGrupo(Long stageId) {
        return grupos.findById(stageId)
                .orElseThrow(() -> new ResourceNotFoundException("Peneira nao encontrada: " + stageId));
    }

    private GrupoFuncionario buscarVinculo(Long stageId, Long candidateId) {
        buscarGrupo(stageId);
        return vinculos.findByGrupoIdAndFuncionarioId(stageId, candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidato " + candidateId + " nao pertence a peneira " + stageId));
    }
}
