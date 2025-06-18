package br.com.gestao_bancaria.mapper;

import br.com.gestao_bancaria.modules.Conta;
import br.com.gestao_bancaria.modules.Pessoa;
import br.com.gestao_bancaria.records.conta.ContaDetailsRecord;
import br.com.gestao_bancaria.records.conta.ContaRecord;
import br.com.gestao_bancaria.repositories.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ContaMapper {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Mapping(target = "idPessoa", source = "pessoa")
    public abstract ContaRecord toDto(Conta entity);

    @Mapping(target = "pessoa", source = "idPessoa")
    public abstract Conta toEntity(ContaRecord dto);

    @Mapping(target = "id", ignore = true)
    public abstract void updateConta(ContaRecord dto, @MappingTarget Conta entity);

    public abstract List<ContaDetailsRecord> toDtoList(List<Conta> listEntity);

    Pessoa mapLongToPessoa(Long id) {
        return this.pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
    }

    Long mapPessoatoLong(Pessoa entity) {
        return entity.getId();
    }
}
