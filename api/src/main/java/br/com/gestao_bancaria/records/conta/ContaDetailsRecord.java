package br.com.gestao_bancaria.records.conta;

import br.com.gestao_bancaria.records.pessoa.PessoaRecord;

public record ContaDetailsRecord(Long id,
                                 PessoaRecord pessoa,
                                 String numeroConta) {
}
