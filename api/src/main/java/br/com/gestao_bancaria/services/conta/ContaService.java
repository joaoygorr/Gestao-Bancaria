package br.com.gestao_bancaria.services.conta;

import br.com.gestao_bancaria.modules.Conta;
import br.com.gestao_bancaria.records.conta.ContaRecord;

import java.util.List;

public interface ContaService {

    Conta createConta(Conta entity);

    List<Conta> findAllAccount();

    void deleteAccount(Long id);

    Conta findById(Long id);

    Conta updateAccount(Long id, ContaRecord dto);
}
