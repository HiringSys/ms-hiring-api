package com.example.hiringsys.repository;

import com.example.hiringsys.entity.GrupoFuncionario;
import com.example.hiringsys.enums.StatusEnvioEmail;
import com.example.hiringsys.enums.StatusSelecao;
import com.example.hiringsys.enums.TipoEmail;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GrupoFuncionarioRepository extends JpaRepository<GrupoFuncionario, Long> {
    List<GrupoFuncionario> findByFuncionarioId(Long funcionarioId);
    @EntityGraph(attributePaths = {"grupo", "funcionario", "funcionario.cargos", "funcionario.redes", "funcionario.arquivos"})
    List<GrupoFuncionario> findByGrupoIdOrderByOrdemAprovacaoAsc(Long grupoId);
    List<GrupoFuncionario> findByGrupoId(Long grupoId);
    Optional<GrupoFuncionario> findByGrupoIdAndFuncionarioId(Long grupoId, Long funcionarioId);
    boolean existsByGrupoIdAndFuncionarioId(Long grupoId, Long funcionarioId);
    long countByGrupoId(Long grupoId);

    @Query("""
            select vinculo.grupo.id as grupoId, count(vinculo.id) as quantidade
            from GrupoFuncionario vinculo
            where vinculo.grupo.id in :grupoIds
            group by vinculo.grupo.id
            """)
    List<ContagemParticipantes> contarParticipantesPorGrupo(
            @Param("grupoIds") Set<Long> grupoIds
    );

    interface ContagemParticipantes {
        Long getGrupoId();
        long getQuantidade();
    }

    @Query("""
            select vinculo.id
            from GrupoFuncionario vinculo
            where vinculo.statusSelecao = :statusSelecao
              and not exists (
                  select log.id
                  from EmailEnvioLog log
                  where log.grupoFuncionarioId = vinculo.id
                    and log.tipo = :tipoEmail
                    and (
                        log.status = :statusEnviado
                        or log.status = :statusProcessando
                        or log.tentativas >= :maxTentativas
                    )
              )
            order by vinculo.id
            """)
    List<Long> findIdsPendentesDeNotificacao(
            @Param("statusSelecao") StatusSelecao statusSelecao,
            @Param("statusEnviado") StatusEnvioEmail statusEnviado,
            @Param("statusProcessando") StatusEnvioEmail statusProcessando,
            @Param("tipoEmail") TipoEmail tipoEmail,
            @Param("maxTentativas") int maxTentativas,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"grupo", "funcionario"})
    @Query("select vinculo from GrupoFuncionario vinculo where vinculo.id = :id")
    Optional<GrupoFuncionario> findByIdForUpdate(@Param("id") Long id);
}
