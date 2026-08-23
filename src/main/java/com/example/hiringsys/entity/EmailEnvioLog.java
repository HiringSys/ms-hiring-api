package com.example.hiringsys.entity;

import com.example.hiringsys.enums.StatusEnvioEmail;
import com.example.hiringsys.enums.TipoEmail;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "email_envio_log",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_email_envio_log_vinculo_tipo",
                columnNames = {"grupo_funcionario_id", "tipo"}
        )
)
public class EmailEnvioLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grupo_funcionario_id", nullable = false)
    private Long grupoFuncionarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEmail tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEnvioEmail status;

    @Column(nullable = false, length = 150)
    private String destinatario;

    @Column(nullable = false)
    private Integer tentativas;

    @Column(name = "ultimo_erro", length = 1000)
    private String ultimoErro;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @Column(name = "enviado_em")
    private Instant enviadoEm;

    @PrePersist
    void prePersist() {
        Instant agora = Instant.now();
        if (tentativas == null) tentativas = 0;
        if (criadoEm == null) criadoEm = agora;
        atualizadoEm = agora;
    }

    @PreUpdate
    void preUpdate() {
        atualizadoEm = Instant.now();
    }

    public void iniciarTentativa(String email) {
        destinatario = email;
        status = StatusEnvioEmail.PROCESSANDO;
        tentativas = (tentativas == null ? 0 : tentativas) + 1;
        ultimoErro = null;
    }

    public void marcarEnviado() {
        status = StatusEnvioEmail.ENVIADO;
        enviadoEm = Instant.now();
        ultimoErro = null;
    }

    public void marcarFalha(String erro) {
        status = StatusEnvioEmail.FALHA;
        ultimoErro = limitar(erro, 1000);
    }

    private String limitar(String texto, int limite) {
        if (texto == null || texto.length() <= limite) return texto;
        return texto.substring(0, limite);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGrupoFuncionarioId() { return grupoFuncionarioId; }
    public void setGrupoFuncionarioId(Long grupoFuncionarioId) { this.grupoFuncionarioId = grupoFuncionarioId; }
    public TipoEmail getTipo() { return tipo; }
    public void setTipo(TipoEmail tipo) { this.tipo = tipo; }
    public StatusEnvioEmail getStatus() { return status; }
    public void setStatus(StatusEnvioEmail status) { this.status = status; }
    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
    public Integer getTentativas() { return tentativas; }
    public void setTentativas(Integer tentativas) { this.tentativas = tentativas; }
    public String getUltimoErro() { return ultimoErro; }
    public Instant getCriadoEm() { return criadoEm; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
    public Instant getEnviadoEm() { return enviadoEm; }
}
