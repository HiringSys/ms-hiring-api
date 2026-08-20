package com.example.hiringsys.service;

import com.example.hiringsys.entity.Departamento;
import com.example.hiringsys.exception.ResourceNotFoundException;
import com.example.hiringsys.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartamentoService {

    private final DepartamentoRepository repository;

    public DepartamentoService(DepartamentoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Departamento> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Departamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado: " + id));
    }

    @Transactional
    public Departamento salvar(Departamento departamento) {
        departamento.setId(null);
        return repository.save(departamento);
    }

    @Transactional
    public Departamento atualizar(Long id, Departamento dados) {
        Departamento departamento = buscarPorId(id);
        departamento.setNome(dados.getNome());
        return repository.save(departamento);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarPorId(id));
    }
}
