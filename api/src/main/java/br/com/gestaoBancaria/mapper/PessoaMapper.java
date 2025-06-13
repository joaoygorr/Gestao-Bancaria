package br.com.gestaoBancaria.mapper;

import br.com.gestaoBancaria.modules.Pessoa;
import br.com.gestaoBancaria.records.pessoa.PessoaRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PessoaMapper {

    public abstract PessoaRecord toDto(Pessoa entity);

    public  abstract Pessoa toEntity(PessoaRecord dto);

    public abstract List<PessoaRecord> toDtoList(List<Pessoa> listEntity);

    @Mapping(target = "id", ignore = true)
    public abstract void updatePessoa(PessoaRecord dto, @MappingTarget Pessoa entity);
}
