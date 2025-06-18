package br.com.gestao_bancaria.records.pessoa;

import br.com.gestao_bancaria.records.conta.ContaComMovimentacao;

import java.util.List;

public record PessoaComContasRecord(PessoaRecord pessoa, List<ContaComMovimentacao> contas) {
}
