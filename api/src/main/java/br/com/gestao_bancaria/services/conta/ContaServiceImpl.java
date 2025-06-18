package br.com.gestao_bancaria.services.conta;

import br.com.gestao_bancaria.configuration.execptions.Exception400;
import br.com.gestao_bancaria.configuration.execptions.Exception404;
import br.com.gestao_bancaria.configuration.execptions.Exception409;
import br.com.gestao_bancaria.mapper.ContaMapper;
import br.com.gestao_bancaria.modules.Conta;
import br.com.gestao_bancaria.records.conta.ContaRecord;
import br.com.gestao_bancaria.repositories.ContaRepository;
import br.com.gestao_bancaria.repositories.MovimentacaoRepository;
import br.com.gestao_bancaria.services.pessoa.PessoaService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContaServiceImpl implements ContaService {

    private final ContaRepository contaRepository;

    private final PessoaService pessoaService;

    private final MovimentacaoRepository movimentacaoRepository;

    private final ContaMapper contaMapper;

    @Transactional
    @Override
    public Conta createConta(Conta entity) {
        try {
            this.pessoaService.findById(entity.getPessoa().getId());
            return this.contaRepository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new Exception409("Conta já cadastrada");
        }
    }

    @Override
    public List<Conta> findAllAccount() {
        return this.contaRepository.findAll();
    }

    @Override
    public Conta findById(Long id) {
        return this.contaRepository.findById(id).orElseThrow(() -> new Exception404("Conta não encontrada"));
    }

    @Transactional
    @Override
    public void deleteAccount(Long id) {
        Conta entity = findById(id);
        if (this.movimentacaoRepository.existsByContaId(entity.getId())) {
            throw new Exception400("Não é possível excluir uma conta com movimentação vinculada");
        }
        this.contaRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Conta updateAccount(Long id, ContaRecord dto) {
        Conta entity = findById(id);
        this.contaMapper.updateConta(dto, entity);
        return entity;
    }
}
