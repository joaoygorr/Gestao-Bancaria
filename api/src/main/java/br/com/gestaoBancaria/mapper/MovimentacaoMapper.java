package br.com.gestaoBancaria.mapper;

import br.com.gestaoBancaria.modules.Conta;
import br.com.gestaoBancaria.modules.Movimentacao;
import br.com.gestaoBancaria.modules.Pessoa;
import br.com.gestaoBancaria.records.movimentacao.MovimentacaoDetails;
import br.com.gestaoBancaria.records.movimentacao.MovimentacaoRecord;
import br.com.gestaoBancaria.repositories.ContaRepository;
import br.com.gestaoBancaria.repositories.PessoaRepository;
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
