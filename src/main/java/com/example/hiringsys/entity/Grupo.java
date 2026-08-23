package com.example.hiringsys.entity;

import com.example.hiringsys.enums.EstadoGrupo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "grupo")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String area;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoGrupo estado;

    @Column(nullable = false)
    private Integer disponiveis;

    @Column(length = 100)
    private String cargo;

    @Column(name = "limite_aprovados", nullable = false)
    private Integer limiteAprovados;

    @Column(name = "email_equipe", nullable = false, length = 150)
    private String emailEquipe;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void prePersist() {
        if (estado == null) estado = EstadoGrupo.RASCUNHO;
        if (disponiveis == null) disponiveis = 0;
        if (limiteAprovados == null) limiteAprovados = 0;
        if (emailEquipe == null) emailEquipe = "rh@hiringsys.local";
        if (criadoEm == null) criadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public EstadoGrupo getEstado() { return estado; }
    public void setEstado(EstadoGrupo estado) { this.estado = estado; }
    public Integer getDisponiveis() { return disponiveis; }
    public void setDisponiveis(Integer disponiveis) { this.disponiveis = disponiveis; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public Integer getLimiteAprovados() { return limiteAprovados; }
    public void setLimiteAprovados(Integer limiteAprovados) { this.limiteAprovados = limiteAprovados; }
    public String getEmailEquipe() { return emailEquipe; }
    public void setEmailEquipe(String emailEquipe) { this.emailEquipe = emailEquipe; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
