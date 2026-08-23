package com.example.hiringsys.service;

import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.exception.BusinessRuleException;
import com.example.hiringsys.exception.ResourceNotFoundException;
import com.example.hiringsys.repository.CargoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CargoService {

    private final CargoRepository repository;

    public CargoService(CargoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Cargo> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Cargo buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Cargo> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional
    public Cargo salvar(Cargo cargo) {
        cargo.setId(null);
        validarNome(cargo.getNome(), null);
        return repository.save(cargo);
    }

    @Transactional
    public Cargo atualizar(Long id, Cargo dados) {
        Cargo cargo = buscarPorId(id);
        validarNome(dados.getNome(), id);
        cargo.setNome(dados.getNome());
        return repository.save(cargo);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarPorId(id));
    }

    private void validarNome(String nome, Long id) {
        boolean nomeEmUso = id == null
                ? repository.existsByNomeIgnoreCase(nome)
                : repository.existsByNomeIgnoreCaseAndIdNot(nome, id);

        if (nomeEmUso) {
            throw new BusinessRuleException("Cargo já cadastrado: " + nome);
        }
    }
}
