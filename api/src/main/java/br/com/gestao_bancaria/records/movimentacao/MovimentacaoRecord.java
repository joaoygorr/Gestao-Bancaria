package br.com.gestao_bancaria.records.movimentacao;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoRecord(@Hidden Long id,
                                 @NotNull(message = "Pessoa não pode ser nula") Long pessoa,
                                 @NotNull(message = "Data não pode ser nula")
                                 @JsonFormat(pattern = "dd/MM/yyyy, HH:mm:ss", timezone = "America/Cuiaba")
                                 LocalDateTime data,
                                 @NotNull(message = "Conta não pode ser nula") Long conta,
                                 @NotNull(message = "Valor não pode ser nulo") BigDecimal valor,
                                 @NotBlank(message = "Tipo Movimentação em branco") String tipo) {
}
