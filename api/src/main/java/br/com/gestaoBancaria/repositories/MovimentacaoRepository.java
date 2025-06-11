package br.com.gestaoBancaria.repositories;

import br.com.gestaoBancaria.modules.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findAllByPessoaIdAndContaId(Long pessoaId, Long contaId);

    List<Movimentacao> findAllByPessoaId(Long pessoaId);

    boolean existsByContaId(Long id);
}
