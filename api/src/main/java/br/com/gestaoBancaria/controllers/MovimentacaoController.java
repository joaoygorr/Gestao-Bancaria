package br.com.gestaoBancaria.controllers;

import br.com.gestaoBancaria.mapper.MovimentacaoMapper;
import br.com.gestaoBancaria.records.movimentacao.MovimentacaoDetails;
import br.com.gestaoBancaria.records.movimentacao.MovimentacaoRecord;
import br.com.gestaoBancaria.services.movimentacao.MovimentacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimentacao")
@Tag(name = "Movimentação", description = "Endpoints relacionados à entidade Movimentação")
@RequiredArgsConstructor
public class MovimentacaoController {

    private final MovimentacaoMapper movimentacaoMapper;

    private final MovimentacaoService movimentacaoService;

    @PostMapping("/create")
    @Operation(summary = "Criar uma nova movimentação", description = "Recebe os dados de uma movimentação e salva no banco de dados.")
    public ResponseEntity<MovimentacaoRecord> post(@RequestBody @Valid MovimentacaoRecord dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.movimentacaoMapper.toDto(this.movimentacaoService.createMovement(
                this.movimentacaoMapper.toEntity(dto))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Listar todas as movimentações de uma pessoa", description = "Retorna uma lista com todas as movimentações de uma pessoa.")
    public ResponseEntity<List<MovimentacaoDetails>> getAll(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.movimentacaoMapper.toDtoList(this.movimentacaoService.findAllMovementByPerson(id)));
    }
}
