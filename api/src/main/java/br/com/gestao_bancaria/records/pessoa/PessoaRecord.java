package br.com.gestao_bancaria.records.pessoa;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;

public record PessoaRecord(@Hidden Long id,
                           @NotBlank(message = "Nome em branco") String nome,
                           @NotBlank(message = "CPF em branco") String cpf,
                           @NotBlank(message = "Endereço em branco") String endereco) {
}
