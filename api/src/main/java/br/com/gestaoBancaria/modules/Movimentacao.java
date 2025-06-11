package br.com.gestaoBancaria.modules;

import br.com.gestaoBancaria.modules.enums.TipoMovimentacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table(name = "Movimentacoes")
@Entity(name = "Movimentacoes")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Movimentacao {

    @Id
    @Column(name = "id_movimentacao")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa pessoa;

    @JsonFormat(pattern = "dd/MM/yyyy, HH:mm:ss", timezone = "America/Cuiaba")
    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_conta", nullable = false)
    private Conta conta;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoMovimentacao tipo;

    @Column(name = "total_valor")
    private BigDecimal totalValor;
}
