package br.com.gestaoBancaria.services.conta;

import br.com.gestaoBancaria.modules.Conta;
import br.com.gestaoBancaria.records.conta.ContaRecord;

import java.util.List;

public interface ContaService {

    Conta createConta(Conta entity);

    List<Conta> findAllAccount();

    void deleteAccount(Long id);

    Conta findById(Long id);

    Conta updateAccount(Long id, ContaRecord dto);
}
