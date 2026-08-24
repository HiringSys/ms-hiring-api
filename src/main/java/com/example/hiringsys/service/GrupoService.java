package com.example.hiringsys.service;

import com.example.hiringsys.entity.*;
import com.example.hiringsys.enums.EstadoGrupo;
import com.example.hiringsys.exception.*;
import com.example.hiringsys.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GrupoService {
    private final GrupoRepository repository;
    private final GrupoFuncionarioRepository vinculos;
    private final FuncionarioRepository funcionarios;
    public GrupoService(GrupoRepository repository, GrupoFuncionarioRepository vinculos, FuncionarioRepository funcionarios) { this.repository=repository; this.vinculos=vinculos; this.funcionarios=funcionarios; }
    @Transactional(readOnly=true) public List<Grupo> listarTodos(){return repository.findAll();}
    @Transactional(readOnly=true) public Grupo buscarPorId(Long id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Grupo não encontrado: "+id));}
    @Transactional(readOnly=true) public List<Grupo> buscarPorNome(String nome){return repository.findByNomeContainingIgnoreCase(nome);}
    @Transactional(readOnly=true) public List<Grupo> buscarPorEstado(EstadoGrupo estado){return repository.findByEstado(estado);}
    @Transactional public Grupo salvar(Grupo grupo){grupo.setId(null);if(grupo.getLimiteAprovados()==null)grupo.setLimiteAprovados(0);if(grupo.getEmailEquipe()==null)grupo.setEmailEquipe("rh@hiringsys.local");validar(grupo,null);return repository.save(grupo);}
    @Transactional public Grupo atualizar(Long id,Grupo dados){Grupo grupo=buscarPorId(id);validar(dados,id);grupo.setNome(dados.getNome());grupo.setArea(dados.getArea());grupo.setEstado(dados.getEstado());grupo.setDisponiveis(dados.getDisponiveis());grupo.setCargo(dados.getCargo());grupo.setLimiteAprovados(dados.getLimiteAprovados()==null?grupo.getLimiteAprovados():dados.getLimiteAprovados());grupo.setEmailEquipe(dados.getEmailEquipe()==null?grupo.getEmailEquipe():dados.getEmailEquipe());return repository.save(grupo);}
    @Transactional public void excluir(Long id){repository.delete(buscarPorId(id));}
    @Transactional(readOnly=true) public List<GrupoFuncionario> listarFuncionarios(Long grupoId){buscarPorId(grupoId);return vinculos.findByGrupoId(grupoId);}
    @Transactional(readOnly=true) public long contarParticipantes(Long grupoId){return vinculos.countByGrupoId(grupoId);}
    @Transactional(readOnly=true) public Map<Long,Long> contarParticipantes(List<Grupo> grupos){
        Set<Long> grupoIds=grupos.stream().map(Grupo::getId).collect(Collectors.toSet());
        if(grupoIds.isEmpty())return Map.of();
        Map<Long,Long> contagens=new LinkedHashMap<>();
        vinculos.contarParticipantesPorGrupo(grupoIds).forEach(contagem ->
                contagens.put(contagem.getGrupoId(),contagem.getQuantidade()));
        return contagens;
    }
    @Transactional public GrupoFuncionario vincular(Long grupoId,Long funcionarioId,BigDecimal score){Grupo grupo=buscarPorId(grupoId);Funcionario funcionario=funcionarios.findById(funcionarioId).orElseThrow(()->new ResourceNotFoundException("Funcionário não encontrado: "+funcionarioId));if(vinculos.existsByGrupoIdAndFuncionarioId(grupoId,funcionarioId))throw new BusinessRuleException("O funcionário já pertence a este grupo");validarScore(score);GrupoFuncionario vinculo=new GrupoFuncionario();vinculo.setGrupo(grupo);vinculo.setFuncionario(funcionario);vinculo.setScoreProximidade(score);return vinculos.save(vinculo);}
    @Transactional public GrupoFuncionario atualizarScore(Long grupoId,Long funcionarioId,BigDecimal score){validarScore(score);GrupoFuncionario vinculo=buscarVinculo(grupoId,funcionarioId);vinculo.setScoreProximidade(score);return vinculos.save(vinculo);}
    @Transactional public void desvincular(Long grupoId,Long funcionarioId){vinculos.delete(buscarVinculo(grupoId,funcionarioId));}
    private GrupoFuncionario buscarVinculo(Long grupoId,Long funcionarioId){return vinculos.findByGrupoIdAndFuncionarioId(grupoId,funcionarioId).orElseThrow(()->new ResourceNotFoundException("Funcionário não vinculado a este grupo"));}
    private void validar(Grupo grupo,Long id){if(grupo.getDisponiveis()!=null&&grupo.getDisponiveis()<0)throw new BusinessRuleException("Vagas disponíveis não podem ser negativas");boolean existe=id==null?repository.existsByNomeIgnoreCase(grupo.getNome()):repository.existsByNomeIgnoreCaseAndIdNot(grupo.getNome(),id);if(existe)throw new BusinessRuleException("Grupo já cadastrado: "+grupo.getNome());}
    private void validarScore(BigDecimal score){if(score!=null&&(score.signum()<0||score.compareTo(new BigDecimal("100.00"))>0))throw new BusinessRuleException("O score deve estar entre 0 e 100 ou ser nulo");}
}
