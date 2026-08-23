package com.example.hiringsys.repository;

import com.example.hiringsys.entity.EmailEnvioLog;
import com.example.hiringsys.enums.TipoEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailEnvioLogRepository extends JpaRepository<EmailEnvioLog, Long> {

    Optional<EmailEnvioLog> findByGrupoFuncionarioIdAndTipo(
            Long grupoFuncionarioId,
            TipoEmail tipo
    );
}
