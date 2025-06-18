package br.com.gestao_bancaria.services.movimentacao;

import br.com.gestao_bancaria.configuration.execptions.Exception400;
import br.com.gestao_bancaria.modules.Movimentacao;
import br.com.gestao_bancaria.modules.enums.TipoMovimentacao;
import br.com.gestao_bancaria.repositories.MovimentacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentacaoServiceImpl implements MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;

    @Override
    public Movimentacao createMovement(Movimentacao entity) {
        BigDecimal saldoAtual = calcularSaldo( entity.getPessoa().getId(), entity.getConta().getId());

        BigDecimal novoSaldo = calcularNovoSaldo(entity, saldoAtual);
        entity.setTotalValor(novoSaldo);
        return this.movimentacaoRepository.save(entity);
    }

    @Override
    public List<Movimentacao> findAllMovementByPerson(Long pessoaId) {
        return this.movimentacaoRepository.findAllByPessoaId(pessoaId);
    }

    @Override
    public BigDecimal calcularSaldo(Long pessoaId, Long contaId) {
        List<Movimentacao> movimentacoes = this.movimentacaoRepository.findAllByPessoaIdAndContaId(pessoaId, contaId);

        BigDecimal depositos = movimentacoes.stream()
                .filter(m -> m.getTipo() == TipoMovimentacao.DEPOSITAR)
                .map(Movimentacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal retiradas = movimentacoes.stream()
                .filter(m -> m.getTipo() == TipoMovimentacao.RETIRAR)
                .map(Movimentacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return depositos.subtract(retiradas);
    }

    private BigDecimal calcularNovoSaldo(Movimentacao entity, BigDecimal saldoAtual) {
        if (entity.getTipo() == TipoMovimentacao.RETIRAR) {
            validarSaldoParaSaque(entity.getValor(), saldoAtual);
            return saldoAtual.subtract(entity.getValor());
        }

        if (entity.getTipo() == TipoMovimentacao.DEPOSITAR) {
            return saldoAtual.add(entity.getValor());
        }

        return saldoAtual;
    }

    private void validarSaldoParaSaque(BigDecimal valorSaque, BigDecimal saldoAtual) {
        if (saldoAtual.compareTo(valorSaque) < 0) {
            throw new Exception400("Saldo insuficiente para saque.");
        }
    }
}
