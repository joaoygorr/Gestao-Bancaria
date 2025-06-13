package br.com.gestaoBancaria.records.movimentacao;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoDetails(Long id,
                                  @JsonFormat(pattern = "dd/MM/yyyy, HH:mm:ss", timezone = "America/Cuiaba")
                                    LocalDateTime data,
                                  BigDecimal valor,
                                  BigDecimal totalValor,
                                  Long pessoa,
                                  String tipo,
                                  Long conta) {
}
