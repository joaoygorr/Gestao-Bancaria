package br.com.gestaoBancaria.services.pessoa;

import br.com.gestaoBancaria.modules.Pessoa;
import br.com.gestaoBancaria.records.pessoa.PessoaComContasRecord;
import br.com.gestaoBancaria.records.pessoa.PessoaRecord;

import java.util.List;

public interface PessoaService {

    Pessoa createPerson(Pessoa entity);

    Pessoa findById(Long id);

    List<Pessoa> findAllPerson();

    void deletePerson(Long id);

    Pessoa updatePerson(Long id, PessoaRecord dto);

    List<PessoaComContasRecord> findAllPessoasWithContas();
}
