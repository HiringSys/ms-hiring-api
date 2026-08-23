package com.example.hiringsys.repository; import com.example.hiringsys.entity.Cargo; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface CargoRepository extends JpaRepository<Cargo,Long>{List<Cargo> findByNomeContainingIgnoreCase(String nome);boolean existsByNomeIgnoreCase(String nome);boolean existsByNomeIgnoreCaseAndIdNot(String nome,Long id);}
