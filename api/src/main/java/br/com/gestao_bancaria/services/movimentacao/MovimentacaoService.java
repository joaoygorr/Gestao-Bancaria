package br.com.gestao_bancaria.services.movimentacao;

import br.com.gestao_bancaria.modules.Movimentacao;

import java.math.BigDecimal;
import java.util.List;

public interface MovimentacaoService {

    Movimentacao createMovement(Movimentacao entity);

    List<Movimentacao> findAllMovementByPerson(Long pessoaId);

    BigDecimal calcularSaldo(Long pessoaId, Long contaId);
}
