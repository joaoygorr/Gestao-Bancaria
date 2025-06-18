package br.com.gestao_bancaria.repositories;

import br.com.gestao_bancaria.modules.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    Optional<Conta> findByPessoaId(Long id);
}
