package br.com.gestao_bancaria.services.pessoa;

import br.com.gestao_bancaria.modules.Pessoa;
import br.com.gestao_bancaria.records.pessoa.PessoaComContasRecord;
import br.com.gestao_bancaria.records.pessoa.PessoaRecord;

import java.util.List;

public interface PessoaService {

    Pessoa createPerson(Pessoa entity);

    Pessoa findById(Long id);

    List<Pessoa> findAllPerson();

    void deletePerson(Long id);

    Pessoa updatePerson(Long id, PessoaRecord dto);

    List<PessoaComContasRecord> findAllPessoasWithContas();
}
