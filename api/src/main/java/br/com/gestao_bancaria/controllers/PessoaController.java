package br.com.gestao_bancaria.controllers;

import br.com.gestao_bancaria.mapper.PessoaMapper;
import br.com.gestao_bancaria.records.pessoa.PessoaComContasRecord;
import br.com.gestao_bancaria.records.pessoa.PessoaRecord;
import br.com.gestao_bancaria.services.pessoa.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pessoa")
@Tag(name = "Pessoa", description = "Endpoints relacionados à entidade Pessoa")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaMapper pessoaMapper;

    private final PessoaService pessoaService;

    @PostMapping("/create")
    @Operation(summary = "Criar uma nova pessoa", description = "Recebe os dados de uma pessoa e salva no banco de dados.")
    public ResponseEntity<PessoaRecord> post(@RequestBody @Valid PessoaRecord dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.pessoaMapper.toDto(this.pessoaService.createPerson(
                this.pessoaMapper.toEntity(dto))));
    }

    @GetMapping
    @Operation(summary = "Listar todas as pessoas", description = "Retorna uma lista com todas as pessoas cadastradas no sistema.")
    public ResponseEntity<List<PessoaRecord>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.pessoaMapper.toDtoList(this.pessoaService.findAllPerson()));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma pessoa", description = "Remove uma pessoa do banco de dados a partir do ID informado.")
    public ResponseEntity<Void> getAll(@PathVariable Long id) {
        this.pessoaService.deletePerson(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma pessoa", description = "Atualiza uma pessoa do banco de dados a partir do ID informado.")
    public ResponseEntity<PessoaRecord> getAll(@PathVariable Long id, @RequestBody @Valid PessoaRecord dto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.pessoaMapper.toDto(this.pessoaService.updatePerson(id,
                dto)));
    }

    @GetMapping("/pessoas/contas")
    @Operation(summary = "Listar todas as pessoas", description = "Retorna uma lista com todas as pessoas cadastradas no sistema.")
    public ResponseEntity<List<PessoaComContasRecord>> getAllPersonWithAccounts() {
        return ResponseEntity.status(HttpStatus.OK).body(this.pessoaService.findAllPessoasWithContas());
    }
}
