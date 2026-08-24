package com.example.hiringsys.service;

public interface EmailService {

    void enviarNovaSenha(String destinatario, String novaSenha);

    void enviarAprovacao(String destinatario, String nomeCandidato, String nomeProcesso);
}
