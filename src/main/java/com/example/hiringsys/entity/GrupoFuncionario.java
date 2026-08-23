package com.example.hiringsys.entity;

import com.example.hiringsys.enums.StatusSelecao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

@Entity
@Table(
        name = "grupo_funcionario",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_grupo_funcionario",
                columnNames = {"grupo_id", "funcionario_id"}
        )
)
public class GrupoFuncionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(name = "score_proximidade", precision = 5, scale = 2)
    private BigDecimal scoreProximidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_selecao", nullable = false, length = 20)
    private StatusSelecao statusSelecao = StatusSelecao.REPROVADO;

    @Column(name = "ordem_aprovacao")
    private Integer ordemAprovacao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
    public BigDecimal getScoreProximidade() { return scoreProximidade; }
    public void setScoreProximidade(BigDecimal scoreProximidade) { this.scoreProximidade = scoreProximidade; }
    public StatusSelecao getStatusSelecao() { return statusSelecao; }
    public void setStatusSelecao(StatusSelecao statusSelecao) { this.statusSelecao = statusSelecao; }
    public Integer getOrdemAprovacao() { return ordemAprovacao; }
    public void setOrdemAprovacao(Integer ordemAprovacao) { this.ordemAprovacao = ordemAprovacao; }
}
