package com.example.hiringsys.entity;

import com.example.hiringsys.enums.ExperienciaFuncionario;
import com.example.hiringsys.enums.StatusFuncionario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "funcionario")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "salario", precision = 12, scale = 2)
    private BigDecimal salario;

    @Column(name = "cidade", length = 100)
    private String cidade;

    @Column(name = "departamento", length = 100)
    private String departamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusFuncionario status;

    @Enumerated(EnumType.STRING)
    @Column(name = "experiencia", nullable = false, length = 30)
    private ExperienciaFuncionario experiencia;

    @Column(name = "anos_experiencia", nullable = false)
    private Integer anosExperiencia;

    @ManyToMany
    @JoinTable(
            name = "cargo_funcionario",
            joinColumns = @JoinColumn(name = "funcionario_id"),
            inverseJoinColumns = @JoinColumn(name = "cargo_id")
    )
    private Set<Cargo> cargos = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "rede_funcionario",
            joinColumns = @JoinColumn(name = "funcionario_id"),
            inverseJoinColumns = @JoinColumn(name = "rede_id")
    )
    private Set<Rede> redes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GrupoFuncionario> grupos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArquivoFuncionario> arquivos = new LinkedHashSet<>();

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        if (status == null) status = StatusFuncionario.EM_ANALISE;
        if (experiencia == null) experiencia = ExperienciaFuncionario.SEM_EXPERIENCIA;
        if (anosExperiencia == null) anosExperiencia = 0;
        if (criadoEm == null) criadoEm = agora;
        atualizadoEm = agora;
    }

    @PreUpdate
    void preUpdate() { atualizadoEm = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public StatusFuncionario getStatus() { return status; }
    public void setStatus(StatusFuncionario status) { this.status = status; }
    public ExperienciaFuncionario getExperiencia() { return experiencia; }
    public void setExperiencia(ExperienciaFuncionario experiencia) { this.experiencia = experiencia; }
    public Integer getAnosExperiencia() { return anosExperiencia; }
    public void setAnosExperiencia(Integer anosExperiencia) { this.anosExperiencia = anosExperiencia; }
    public Set<Cargo> getCargos() { return cargos; }
    public void setCargos(Set<Cargo> cargos) { this.cargos = cargos == null ? new LinkedHashSet<>() : cargos; }
    public Set<Rede> getRedes() { return redes; }
    public void setRedes(Set<Rede> redes) { this.redes = redes == null ? new LinkedHashSet<>() : redes; }
    public Set<GrupoFuncionario> getGrupos() { return grupos; }
    public void setGrupos(Set<GrupoFuncionario> grupos) { this.grupos = grupos == null ? new LinkedHashSet<>() : grupos; }
    public Set<ArquivoFuncionario> getArquivos() { return arquivos; }
    public void setArquivos(Set<ArquivoFuncionario> arquivos) { this.arquivos = arquivos == null ? new LinkedHashSet<>() : arquivos; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
