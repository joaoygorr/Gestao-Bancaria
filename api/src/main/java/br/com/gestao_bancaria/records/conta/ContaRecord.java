package br.com.gestao_bancaria.records.conta;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContaRecord(@Hidden Long id,
                          @NotNull(message = "Pessoa não pode ser null") Long idPessoa,
                          @NotBlank(message = "Número da conta em branco") String numeroConta) {
}
