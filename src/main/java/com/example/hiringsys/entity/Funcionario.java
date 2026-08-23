package com.example.hiringsys.entity;
import com.example.hiringsys.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.*;
@Entity @Table(name="funcionario") public class Funcionario {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=150) private String nome;
 @Column(nullable=false,unique=true,length=150) private String email;
 @Column(length=20) private String telefone; @Column(precision=12,scale=2) private BigDecimal salario; @Column(length=100) private String cidade;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private StatusFuncionario status;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private ExperienciaFuncionario experiencia;
 @ManyToMany @JoinTable(name="cargo_funcionario",joinColumns=@JoinColumn(name="funcionario_id"),inverseJoinColumns=@JoinColumn(name="cargo_id")) private Set<Cargo> cargos=new LinkedHashSet<>();
 @ManyToMany @JoinTable(name="grupo_funcionario",joinColumns=@JoinColumn(name="funcionario_id"),inverseJoinColumns=@JoinColumn(name="grupo_id")) private Set<Grupo> grupos=new LinkedHashSet<>();
 @ManyToMany @JoinTable(name="rede_funcionario",joinColumns=@JoinColumn(name="funcionario_id"),inverseJoinColumns=@JoinColumn(name="rede_id")) private Set<Rede> redes=new LinkedHashSet<>();
 @Column(name="criado_em",nullable=false,updatable=false) private LocalDateTime criadoEm; @Column(name="atualizado_em",nullable=false) private LocalDateTime atualizadoEm;
 @PrePersist void prePersist(){var a=LocalDateTime.now();if(status==null)status=StatusFuncionario.EM_ANALISE;if(experiencia==null)experiencia=ExperienciaFuncionario.SEM_EXPERIENCIA;if(criadoEm==null)criadoEm=a;atualizadoEm=a;} @PreUpdate void preUpdate(){atualizadoEm=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getNome(){return nome;} public void setNome(String v){nome=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getTelefone(){return telefone;} public void setTelefone(String v){telefone=v;} public BigDecimal getSalario(){return salario;} public void setSalario(BigDecimal v){salario=v;} public String getCidade(){return cidade;} public void setCidade(String v){cidade=v;} public StatusFuncionario getStatus(){return status;} public void setStatus(StatusFuncionario v){status=v;} public ExperienciaFuncionario getExperiencia(){return experiencia;} public void setExperiencia(ExperienciaFuncionario v){experiencia=v;} public Set<Cargo> getCargos(){return cargos;} public void setCargos(Set<Cargo> v){cargos=v==null?new LinkedHashSet<>():v;} public Set<Grupo> getGrupos(){return grupos;} public void setGrupos(Set<Grupo> v){grupos=v==null?new LinkedHashSet<>():v;} public Set<Rede> getRedes(){return redes;} public void setRedes(Set<Rede> v){redes=v==null?new LinkedHashSet<>():v;} public Cargo getCargo(){return cargos.stream().findFirst().orElse(null);} public void setCargo(Cargo v){cargos.clear();if(v!=null)cargos.add(v);} public LocalDateTime getCriadoEm(){return criadoEm;} public void setCriadoEm(LocalDateTime v){criadoEm=v;} public LocalDateTime getAtualizadoEm(){return atualizadoEm;} public void setAtualizadoEm(LocalDateTime v){atualizadoEm=v;}
}
