package br.com.gestaoBancaria.repositories;

import br.com.gestaoBancaria.modules.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    Optional<Conta> findByPessoaId(Long id);
}
