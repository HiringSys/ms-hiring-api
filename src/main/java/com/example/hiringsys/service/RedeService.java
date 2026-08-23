package com.example.hiringsys.service;

import com.example.hiringsys.entity.Rede;
import com.example.hiringsys.enums.TipoRede;
import com.example.hiringsys.exception.BusinessRuleException;
import com.example.hiringsys.exception.ResourceNotFoundException;
import com.example.hiringsys.repository.RedeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RedeService {

    private final RedeRepository repository;

    public RedeService(RedeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Rede> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Rede buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rede não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Rede> buscarPorTipo(TipoRede tipo) {
        return repository.findByTipo(tipo);
    }

    @Transactional
    public Rede salvar(Rede rede) {
        rede.setId(null);
        validarUrl(rede.getUrl(), null);
        return repository.save(rede);
    }

    @Transactional
    public Rede atualizar(Long id, Rede dados) {
        Rede rede = buscarPorId(id);
        validarUrl(dados.getUrl(), id);
        rede.setUrl(dados.getUrl());
        rede.setTipo(dados.getTipo());
        return repository.save(rede);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarPorId(id));
    }

    private void validarUrl(String url, Long id) {
        boolean urlEmUso = id == null
                ? repository.existsByUrlIgnoreCase(url)
                : repository.existsByUrlIgnoreCaseAndIdNot(url, id);

        if (urlEmUso) {
            throw new BusinessRuleException("URL já cadastrada: " + url);
        }
    }
}
