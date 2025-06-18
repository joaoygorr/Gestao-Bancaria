package br.com.gestao_bancaria.mapper;

import br.com.gestao_bancaria.modules.Conta;
import br.com.gestao_bancaria.modules.Movimentacao;
import br.com.gestao_bancaria.modules.Pessoa;
import br.com.gestao_bancaria.records.movimentacao.MovimentacaoDetails;
import br.com.gestao_bancaria.records.movimentacao.MovimentacaoRecord;
import br.com.gestao_bancaria.repositories.ContaRepository;
import br.com.gestao_bancaria.repositories.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class MovimentacaoMapper {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ContaRepository contaRepository;

    public abstract MovimentacaoRecord toDto(Movimentacao entity);

    public  abstract Movimentacao toEntity(MovimentacaoRecord dto);

    public abstract List<MovimentacaoDetails> toDtoList(List<Movimentacao> listEntity);

    public abstract List<MovimentacaoRecord> toRecordList(List<Movimentacao> listEntity);

    Pessoa mapLongToPessoa(Long id) {
        return this.pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
    }

    Long mapContatoLong(Pessoa entity) {
        return entity.getId();
    }

    Conta mapLongToConta(Long id) {
        return this.contaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));
    }

    Long mapPessoatoLong(Conta entity) {
        return entity.getId();
    }
}
