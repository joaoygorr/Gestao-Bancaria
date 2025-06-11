package br.com.gestaoBancaria.records.conta;

import br.com.gestaoBancaria.records.movimentacao.MovimentacaoRecord;

import java.math.BigDecimal;
import java.util.List;

public record ContaComMovimentacao(Long id,
                                   String numeroConta,
                                   BigDecimal totalValor,
                                   List<MovimentacaoRecord> movimentacoes) {
}
