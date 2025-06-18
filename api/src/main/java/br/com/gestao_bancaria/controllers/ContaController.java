package br.com.gestao_bancaria.controllers;

import br.com.gestao_bancaria.mapper.ContaMapper;
import br.com.gestao_bancaria.records.conta.ContaDetailsRecord;
import br.com.gestao_bancaria.records.conta.ContaRecord;
import br.com.gestao_bancaria.services.conta.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conta")
@Tag(name = "Conta", description = "Operações relacionadas às contas no sistema")
@RequiredArgsConstructor
public class ContaController {

    private final ContaMapper contaMapper;

    private final ContaService contaService;

    @PostMapping("/create")
    @Operation(summary = "Criar uma nova conta", description = "Cria uma nova conta com os dados fornecidos.")
    public ResponseEntity<ContaRecord> post(@RequestBody @Valid ContaRecord dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.contaMapper.toDto(this.contaService.createConta(
                this.contaMapper.toEntity(dto))));
    }

    @GetMapping
    @Operation(summary = "Listar todas as contas", description = "Retorna uma lista com todas as contas cadastradas.")
    public ResponseEntity<List<ContaDetailsRecord>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(this.contaMapper.toDtoList(this.contaService.findAllAccount()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma conta", description = "Remove uma conta do banco de dados a partir do ID informado.")
    public ResponseEntity<Void> getAll(@PathVariable Long id) {
        this.contaService.deleteAccount(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma conta", description = "Atualiza uma conta do banco de dados a partir do ID informado.")
    public ResponseEntity<ContaRecord> getAll(@PathVariable Long id, @RequestBody @Valid ContaRecord dto) {
        return ResponseEntity.status(HttpStatus.OK).body(this.contaMapper.toDto(this.contaService.updateAccount(id,
                dto)));
    }
}
