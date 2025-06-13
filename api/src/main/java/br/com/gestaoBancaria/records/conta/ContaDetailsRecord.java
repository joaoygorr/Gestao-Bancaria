package br.com.gestaoBancaria.records.conta;

import br.com.gestaoBancaria.records.pessoa.PessoaRecord;

public record ContaDetailsRecord(Long id,
                                 PessoaRecord pessoa,
                                 String numeroConta) {
}
