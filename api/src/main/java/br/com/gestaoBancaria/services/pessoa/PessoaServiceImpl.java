package br.com.gestaoBancaria.services.pessoa;

import br.com.gestaoBancaria.configuration.execptions.Exception400;
import br.com.gestaoBancaria.configuration.execptions.Exception404;
import br.com.gestaoBancaria.configuration.execptions.Exception409;
import br.com.gestaoBancaria.mapper.MovimentacaoMapper;
import br.com.gestaoBancaria.mapper.PessoaMapper;
import br.com.gestaoBancaria.modules.Movimentacao;
import br.com.gestaoBancaria.modules.Pessoa;
import br.com.gestaoBancaria.records.conta.ContaComMovimentacao;
import br.com.gestaoBancaria.records.movimentacao.MovimentacaoRecord;
import br.com.gestaoBancaria.records.pessoa.PessoaComContasRecord;
import br.com.gestaoBancaria.records.pessoa.PessoaRecord;
import br.com.gestaoBancaria.repositories.ContaRepository;
import br.com.gestaoBancaria.repositories.MovimentacaoRepository;
import br.com.gestaoBancaria.repositories.PessoaRepository;
import br.com.gestaoBancaria.services.movimentacao.MovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepository pessoaRepository;

    private final ContaRepository contaRepository;

    private final PessoaMapper pessoaMapper;

    private final MovimentacaoRepository movimentacaoRepository;

    private final MovimentacaoService movimentacaoService;

    private final MovimentacaoMapper movimentacaoMapper;

    @Transactional
    @Override
    public Pessoa createPerson(Pessoa entity) {
        try {
            entity.setCpf(entity.getCpf().replaceAll("\\D", ""));
            return this.pessoaRepository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new Exception409("CPF já cadastrado");
        }
    }

    @Override
    public Pessoa findById(Long id) {
        return this.pessoaRepository.findById(id).orElseThrow(() -> new Exception404("Pessoa não encontrada"));
    }

    @Override
    public List<Pessoa> findAllPerson() {
        return this.pessoaRepository.findAll();
    }

    @Transactional
    @Override
    public void deletePerson(Long id) {
        if (this.contaRepository.findByPessoaId(id).isPresent()) {
            throw  new Exception400("Não é possível excluir uma pessoa com conta vinculada");
        }
        this.pessoaRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Pessoa updatePerson(Long id, PessoaRecord dto) {
        Pessoa entity = findById(id);
        this.pessoaMapper.updatePessoa(dto, entity);
        return entity;
    }

    @Override
    public List<PessoaComContasRecord> findAllPessoasWithContas() {
        List<MovimentacaoRecord> movimentacaoRecords = this.movimentacaoMapper.toRecordList(this.movimentacaoRepository.findAll());
        Map<Long, List<MovimentacaoRecord>> movsPorConta = movimentacaoRecords.stream()
                .collect(Collectors.groupingBy(
                        MovimentacaoRecord::conta,
                        Collectors.toList()
                ));

        Map<Long, List<ContaComMovimentacao>> contasPorPessoa = contaRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        conta -> conta.getPessoa().getId(),
                        Collectors.mapping(conta -> {
                            BigDecimal total = this.movimentacaoService.calcularSaldo(conta.getPessoa().getId(), conta.getId());
                            List<MovimentacaoRecord> movs = movsPorConta.getOrDefault(conta.getId(), List.of());
                            return new ContaComMovimentacao(
                                    conta.getId(),
                                    conta.getNumeroConta(),
                                    total,
                                    movs
                            );
                        }, Collectors.toList())
                ));

        return pessoaRepository.findAll().stream()
                .map(pessoa -> new PessoaComContasRecord(
                        new PessoaRecord(
                                pessoa.getId(),
                                pessoa.getNome(),
                                pessoa.getCpf(),
                                pessoa.getEndereco()
                        ),
                        contasPorPessoa.getOrDefault(pessoa.getId(), List.of())
                ))
                .toList();
    }

}
