package br.com.gestao_bancaria.services.conta;

import br.com.gestao_bancaria.configuration.execptions.Exception400;
import br.com.gestao_bancaria.configuration.execptions.Exception409;
import br.com.gestao_bancaria.mapper.ContaMapper;
import br.com.gestao_bancaria.modules.Conta;
import br.com.gestao_bancaria.modules.Pessoa;
import br.com.gestao_bancaria.records.conta.ContaRecord;
import br.com.gestao_bancaria.repositories.ContaRepository;
import br.com.gestao_bancaria.repositories.MovimentacaoRepository;
import br.com.gestao_bancaria.services.pessoa.PessoaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceImplTest {

    @InjectMocks
    private ContaServiceImpl contaService;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ContaMapper contaMapper;

    @Mock
    private PessoaService pessoaService;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @Test
    @DisplayName("Should account a person and return the saved entity")
    void shouldCreateConta() {
        Pessoa person = new Pessoa(1L, "name", "12345678900", "address");
        Conta input = new Conta(1L, person, "12345");

        when(this.pessoaService.findById(1L)).thenReturn(person);
        when(this.contaRepository.save(input)).thenReturn(input);

        Conta result = this.contaService.createConta(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(person, result.getPessoa());
        assertEquals("12345", result.getNumeroConta());

        verify(this.contaRepository, times(1)).save(input);
        verify(this.pessoaService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw Exception409 when number is already registered")
    void shouldThrowException409OnCpfDuplicate() {
        Pessoa person = new Pessoa(1L, "name", "12345678900", "address");
        Conta input = new Conta(1L, person, "12345");

        when(this.contaRepository.save(input)).thenThrow(new DataIntegrityViolationException("Conta duplicada"));

        Exception409 exception = assertThrows(Exception409.class, () -> this.contaService.createConta(input));

        assertEquals("Conta já cadastrada", exception.getMessage());

        verify(this.contaRepository, times(1)).save(input);
    }

    @Test
    @DisplayName("Should return a list of persons when accounts exist")
    void shouldReturnAllAccounts() {
        Pessoa person = new Pessoa(1L, "name", "12345678900", "address");

        List<Conta> accounts = List.of(
                new Conta(1L, person, "12345"),
                new Conta(2L, person, "54321")
        );

        when(this.contaRepository.findAll()).thenReturn(accounts);

        List<Conta> result = this.contaService.findAllAccount();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.getFirst().getId());
        assertEquals("12345", result.getFirst().getNumeroConta());

        assertEquals(2L, result.getLast().getId());
        assertEquals("54321", result.getLast().getNumeroConta());

        verify(this.contaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return the account when the ID exists")
    void shouldReturnAccountById() {
        Pessoa person = new Pessoa(1L, "name", "12345678900", "address");
        Conta input = new Conta(1L, person, "12345");

        when(this.contaRepository.findById(1L)).thenReturn(Optional.of(input));

        Conta result = this.contaService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(person, result.getPessoa());
        assertEquals("12345", result.getNumeroConta());

        verify(this.contaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should account person by ID")
    void shouldDeleteAccountById() {
        Pessoa person = new Pessoa(1L, "name", "12345678900", "address");
        Conta input = new Conta(1L, person, "12345");
        when(this.contaRepository.findById(1L)).thenReturn(Optional.of(input));

        this.contaService.deleteAccount(1L);

        verify(this.contaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting account with linked movimentation")
    void shouldThrowExceptionWhenDeletingAccountWithLinkedMovimentation() {
        Pessoa person = new Pessoa(1L, "name", "12345678900", "address");
        Conta input = new Conta(1L, person, "12345");

        when(this.contaRepository.findById(input.getId())).thenReturn(Optional.of(input));
        when(this.movimentacaoRepository.existsByContaId(input.getId())).thenReturn(true);

        Exception400 exception = assertThrows(Exception400.class, () -> {
            this.contaService.deleteAccount(1L);
        });

        assertEquals("Não é possível excluir uma conta com movimentação vinculada", exception.getMessage());

        verify(this.contaRepository, never()).deleteById(anyLong());
    }


    @Test
    @DisplayName("Should update account by ID")
    void shouldUpdateAccountById() {
        Pessoa person = new Pessoa(1L, "name", "12345678900", "address");
        Conta input = new Conta(1L, person, "12345");

        ContaRecord contaRecord = new ContaRecord(1L, person.getId(), "12345");

        when(this.contaRepository.findById(1L)).thenReturn(Optional.of(input));
        doAnswer(invocation -> {
            ContaRecord dto = invocation.getArgument(0);
            Conta entity = invocation.getArgument(1);
            entity.setPessoa(person);
            entity.setNumeroConta(dto.numeroConta());
            return null;
        }).when(this.contaMapper).updateConta(contaRecord, input);

        Conta updatedConta = this.contaService.updateAccount(1L, contaRecord);

        assertNotNull(updatedConta);
        assertEquals(1L, updatedConta.getId());
        assertEquals(person, updatedConta.getPessoa());
        assertEquals("12345", updatedConta.getNumeroConta());

        verify(this.contaMapper, times(1)).updateConta(contaRecord, input);
    }
}
