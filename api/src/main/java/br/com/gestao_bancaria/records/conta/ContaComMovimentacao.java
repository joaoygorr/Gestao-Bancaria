package br.com.gestao_bancaria.records.conta;

import br.com.gestao_bancaria.records.movimentacao.MovimentacaoRecord;

import java.math.BigDecimal;
import java.util.List;

public record ContaComMovimentacao(Long id,
                                   String numeroConta,
                                   BigDecimal totalValor,
                                   List<MovimentacaoRecord> movimentacoes) {
}
