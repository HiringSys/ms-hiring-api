package com.example.hiringsys.entity;
import jakarta.persistence.*;
@Entity @Table(name="cargo") public class Cargo {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=100) private String nome;
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getNome(){return nome;} public void setNome(String v){nome=v;}
}
