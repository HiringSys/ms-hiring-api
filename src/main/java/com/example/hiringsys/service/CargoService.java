package com.example.hiringsys.service;

import com.example.hiringsys.entity.Cargo;
import com.example.hiringsys.entity.Departamento;
import com.example.hiringsys.exception.BusinessRuleException;
import com.example.hiringsys.exception.ResourceNotFoundException;
import com.example.hiringsys.repository.CargoRepository;
import com.example.hiringsys.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CargoService {

    private final CargoRepository repository;
    private final DepartamentoRepository departamentoRepository;

    public CargoService(CargoRepository repository, DepartamentoRepository departamentoRepository) {
        this.repository = repository;
        this.departamentoRepository = departamentoRepository;
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

    @Transactional(readOnly = true)
    public List<Cargo> buscarPorDepartamento(Long departamentoId) {
        Departamento departamento = buscarDepartamento(departamentoId);
        return repository.findByDepartamento(departamento);
    }

    @Transactional
    public Cargo salvar(Cargo cargo) {
        cargo.setId(null);
        cargo.setDepartamento(validarDepartamento(cargo));
        return repository.save(cargo);
    }

    @Transactional
    public Cargo atualizar(Long id, Cargo dados) {
        Cargo cargo = buscarPorId(id);
        cargo.setNome(dados.getNome());
        cargo.setDepartamento(validarDepartamento(dados));
        return repository.save(cargo);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarPorId(id));
    }

    private Departamento validarDepartamento(Cargo cargo) {
        if (cargo.getDepartamento() == null || cargo.getDepartamento().getId() == null) {
            throw new BusinessRuleException("O departamento do cargo é obrigatório");
        }
        return buscarDepartamento(cargo.getDepartamento().getId());
    }

    private Departamento buscarDepartamento(Long id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado: " + id));
    }
}
