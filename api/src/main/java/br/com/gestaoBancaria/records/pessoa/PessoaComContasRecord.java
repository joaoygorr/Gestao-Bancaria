package br.com.gestaoBancaria.records.pessoa;

import br.com.gestaoBancaria.records.conta.ContaComMovimentacao;

import java.util.List;

public record PessoaComContasRecord(PessoaRecord pessoa, List<ContaComMovimentacao> contas) {
}
