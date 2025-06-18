package br.com.gestao_bancaria.services.movimentacao;

import br.com.gestao_bancaria.configuration.execptions.Exception400;
import br.com.gestao_bancaria.modules.Conta;
import br.com.gestao_bancaria.modules.Movimentacao;
import br.com.gestao_bancaria.modules.Pessoa;
import br.com.gestao_bancaria.modules.enums.TipoMovimentacao;
import br.com.gestao_bancaria.repositories.MovimentacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimentacaoServiceImplTest {

    @InjectMocks
    private MovimentacaoServiceImpl movimentacaoService;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @Test
    @DisplayName("Should create a movimentation and return the saved entity with updated balance")
    void shouldCreateMovementWithUpdatedBalance() {
        Pessoa person = new Pessoa(1L, "name", "12345678900", "address");
        Conta account = new Conta(1L, person, "12345");

        Movimentacao input = new Movimentacao(1L, person, LocalDateTime.now(), account, new BigDecimal("100.00"),
                TipoMovimentacao.DEPOSITAR,
                null);

        when(this.movimentacaoRepository.save(input)).thenReturn(input);

        Movimentacao result = this.movimentacaoService.createMovement(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(person, result.getPessoa());
        assertEquals(account, result.getConta());
        assertEquals(new BigDecimal("100.00"), result.getValor());
        assertEquals(TipoMovimentacao.DEPOSITAR, result.getTipo());

        assertNotNull(result.getTotalValor());

        verify(this.movimentacaoRepository, times(1)).save(input);
    }

    @Test
    @DisplayName("Should return all movements for a given person ID")
    void shouldReturnAllMovementsForPerson() {
        Long pessoaId = 1L;
        Pessoa person = new Pessoa(pessoaId, "name", "12345678900", "address");
        Conta account = new Conta(1L, person, "12345");

        List<Movimentacao> movements = List.of(
                new Movimentacao(1L, person, LocalDateTime.now(), account, new BigDecimal("100.00"),
                        TipoMovimentacao.DEPOSITAR, new BigDecimal("100.00")),
                new Movimentacao(2L, person, LocalDateTime.now(), account, new BigDecimal("50.00"),
                        TipoMovimentacao.RETIRAR, new BigDecimal("50.00"))
        );

        when(this.movimentacaoRepository.findAllByPessoaId(pessoaId)).thenReturn(movements);

        List<Movimentacao> result = this.movimentacaoService.findAllMovementByPerson(pessoaId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(movements, result);

        verify(this.movimentacaoRepository, times(1)).findAllByPessoaId(pessoaId);
    }

    @Test
    @DisplayName("Should correctly calculate balance for given person and account")
    void shouldCalculateSaldo() {
        Pessoa person = new Pessoa(1L, "name", "12345678900", "address");
        Conta account = new Conta(1L, person, "12345");

        List<Movimentacao> movimentacoes = List.of(
                new Movimentacao(1L, person, LocalDateTime.now(), account, new BigDecimal("500.00"),
                        TipoMovimentacao.DEPOSITAR, new BigDecimal("500.00")),
                new Movimentacao(2L, person, LocalDateTime.now(), account, new BigDecimal("200.00"),
                        TipoMovimentacao.RETIRAR, new BigDecimal("300.00")),
                new Movimentacao(3L, person, LocalDateTime.now(), account, new BigDecimal("100.00"),
                        TipoMovimentacao.DEPOSITAR, new BigDecimal("400.00"))
        );

        when(this.movimentacaoRepository.findAllByPessoaIdAndContaId(1L, 1L)).thenReturn(movimentacoes);

        BigDecimal saldo = this.movimentacaoService.calcularSaldo(1L, 1L);

        assertEquals(new BigDecimal("400.00"), saldo);

        verify(this.movimentacaoRepository, times(1)).findAllByPessoaIdAndContaId(1L, 1L);
    }

    @Test
    @DisplayName("Should create a withdraw movimentation when balance is sufficient")
    void shouldCreateWithdrawMovementWithSufficientBalance() {
        Pessoa person = new Pessoa(1L, "name", "12345678900", "address");
        Conta account = new Conta(1L, person, "12345");

        Movimentacao input = new Movimentacao(1L, person, LocalDateTime.now(), account, new BigDecimal("200.00"),
                TipoMovimentacao.RETIRAR,
                null);

        when(this.movimentacaoRepository.findAllByPessoaIdAndContaId(person.getId(), account.getId()))
                .thenReturn(List.of(
                        new Movimentacao(2L, person, LocalDateTime.now(), account, new BigDecimal("500.00"),
                                TipoMovimentacao.DEPOSITAR, new BigDecimal("500.00"))
                ));
        when(this.movimentacaoRepository.save(any(Movimentacao.class))).thenReturn(input);

        Movimentacao result = this.movimentacaoService.createMovement(input);

        assertNotNull(result);
        assertEquals(new BigDecimal("300.00"), result.getTotalValor());
        verify(this.movimentacaoRepository, times(1)).save(any(Movimentacao.class));
    }

    @Test
    @DisplayName("Should create movimentation and return current balance when type is unknown")
    void shouldReturnCurrentBalanceIfUnknownType() {
        Pessoa person = new Pessoa(1L, "name", "12345678900", "address");
        Conta account = new Conta(1L, person, "12345");

        Movimentacao input = new Movimentacao(1L, person, LocalDateTime.now(), account, new BigDecimal("100.00"),
                null, null);

        when(this.movimentacaoRepository.findAllByPessoaIdAndContaId(person.getId(), account.getId())).thenReturn(List.of());
        when(this.movimentacaoRepository.save(input)).thenReturn(input);

        Movimentacao result = this.movimentacaoService.createMovement(input);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalValor());
        verify(this.movimentacaoRepository, times(1)).save(input);
    }

    @Test
    @DisplayName("Should throw Exception400 when withdrawing with insufficient balance")
    void shouldThrowExceptionWhenWithdrawWithInsufficientBalance() {
        Pessoa person = new Pessoa(1L, "name", "12345678900", "address");
        Conta account = new Conta(1L, person, "12345");

        Movimentacao input = new Movimentacao(1L, person, LocalDateTime.now(), account, new BigDecimal("1000.00"),
                TipoMovimentacao.RETIRAR,
                null);

        when(this.movimentacaoRepository.findAllByPessoaIdAndContaId(person.getId(), account.getId()))
                .thenReturn(List.of(
                        new Movimentacao(2L, person, LocalDateTime.now(), account, new BigDecimal("500.00"),
                                TipoMovimentacao.DEPOSITAR, new BigDecimal("500.00"))
                ));

        Exception400 exception = assertThrows(Exception400.class, () -> this.movimentacaoService.createMovement(input));
        assertEquals("Saldo insuficiente para saque.", exception.getMessage());

        verify(this.movimentacaoRepository, never()).save(any(Movimentacao.class));
    }

}