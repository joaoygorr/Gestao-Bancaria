package br.com.gestaoBancaria.services.pessoa;

import br.com.gestaoBancaria.configuration.execptions.Exception400;
import br.com.gestaoBancaria.configuration.execptions.Exception409;
import br.com.gestaoBancaria.mapper.MovimentacaoMapper;
import br.com.gestaoBancaria.mapper.PessoaMapper;
import br.com.gestaoBancaria.modules.Conta;
import br.com.gestaoBancaria.modules.Movimentacao;
import br.com.gestaoBancaria.modules.Pessoa;
import br.com.gestaoBancaria.modules.enums.TipoMovimentacao;
import br.com.gestaoBancaria.records.conta.ContaComMovimentacao;
import br.com.gestaoBancaria.records.movimentacao.MovimentacaoRecord;
import br.com.gestaoBancaria.records.pessoa.PessoaComContasRecord;
import br.com.gestaoBancaria.records.pessoa.PessoaRecord;
import br.com.gestaoBancaria.repositories.ContaRepository;
import br.com.gestaoBancaria.repositories.MovimentacaoRepository;
import br.com.gestaoBancaria.repositories.PessoaRepository;
import br.com.gestaoBancaria.services.movimentacao.MovimentacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PessoaServiceImplTest {

    @Autowired
    @InjectMocks
    private PessoaServiceImpl pessoaService;

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private PessoaMapper pessoaMapper;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @Mock
    private MovimentacaoMapper movimentacaoMapper;

    @Mock
    private MovimentacaoService movimentacaoService;

    @Test
    @DisplayName("Should create a person and return the saved entity")
    void shouldCreatePerson() {
        Pessoa input = new Pessoa(1L, "name", "12345678900", "address");
        when(this.pessoaRepository.save(input)).thenReturn(input);

        Pessoa result = this.pessoaService.createPerson(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("name", result.getNome());
        assertEquals("12345678900", result.getCpf());
        assertEquals("address", result.getEndereco());

        verify(this.pessoaRepository, times(1)).save(input);
    }

    @Test
    @DisplayName("Should throw Exception409 when CPF is already registered")
    void shouldThrowException409OnCpfDuplicate() {
        Pessoa input = new Pessoa(1L, "name", "12345678900", "address");

        when(pessoaRepository.save(input)).thenThrow(new DataIntegrityViolationException("CPF duplicado"));

        Exception409 exception = assertThrows(Exception409.class, () -> {
            pessoaService.createPerson(input);
        });

        assertEquals("CPF já cadastrado", exception.getMessage());

        verify(this.pessoaRepository, times(1)).save(input);
    }

    @Test
    @DisplayName("Should return the person when the ID exists")
    void shouldReturnPersonById() {
        Pessoa input = new Pessoa(1L, "name", "12345678900", "address");
        when(this.pessoaRepository.findById(1L)).thenReturn(Optional.of(input));

        Pessoa result = this.pessoaService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("name", result.getNome());
        assertEquals("12345678900", result.getCpf());
        assertEquals("address", result.getEndereco());

        verify(this.pessoaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return a list of persons when persons exist")
    void shouldReturnAllPersons() {
        List<Pessoa> pessoas = List.of(
                new Pessoa(1L, "John", "12345678900", "Address 1"),
                new Pessoa(2L, "Jane", "98765432100", "Address 2")
        );

        when(this.pessoaRepository.findAll()).thenReturn(pessoas);

        List<Pessoa> result = this.pessoaService.findAllPerson();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("John", result.get(0).getNome());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Jane", result.get(1).getNome());

        verify(this.pessoaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should delete person by ID")
    void shouldDeletePersonById() {
        when(this.contaRepository.findByPessoaId(1L)).thenReturn(Optional.empty());

        this.pessoaService.deletePerson(1L);

        verify(this.pessoaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting person with linked account")
    void shouldThrowExceptionWhenDeletingPersonWithLinkedAccount() {
        when(this.contaRepository.findByPessoaId(1L)).thenReturn(Optional.of(new Conta()));

        Exception400 exception = assertThrows(Exception400.class, () -> {
            this.pessoaService.deletePerson(1L);
        });

        assertEquals("Não é possível excluir uma pessoa com conta vinculada", exception.getMessage());

        verify(this.pessoaRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should update person by ID")
    void shouldUpdatePersonById() {
        Pessoa input = new Pessoa(1L, "John", "12345678900", "Address 1");

        PessoaRecord pessoaRecord = new PessoaRecord(1L, "joao", "12345678900", "Address 2");

        when(this.pessoaRepository.findById(1L)).thenReturn(Optional.of(input));
        doAnswer(invocation -> {
            PessoaRecord dto = invocation.getArgument(0);
            Pessoa entity = invocation.getArgument(1);
            entity.setNome(dto.nome());
            entity.setCpf(dto.cpf());
            entity.setEndereco(dto.endereco());
            return null;
        }).when(this.pessoaMapper).updatePessoa(pessoaRecord, input);

        Pessoa updatedPerson = this.pessoaService.updatePerson(1L, pessoaRecord);

        assertNotNull(updatedPerson);
        assertEquals(1L, updatedPerson.getId());
        assertEquals("joao", updatedPerson.getNome());
        assertEquals("12345678900", updatedPerson.getCpf());
        assertEquals("Address 2", updatedPerson.getEndereco());

        verify(this.pessoaMapper, times(1)).updatePessoa(pessoaRecord, input);
    }

    @Test
    @DisplayName("Should return all persons with their accounts and transactions")
    void shouldReturnAllPersonsWithAccountsAndTransactions() {
        Pessoa pessoa = new Pessoa(1L, "John Doe", "12345678900", "Address 1");

        Conta conta = new Conta(1L, pessoa, "0001");

        Movimentacao movimentacao = new Movimentacao(1L, pessoa, LocalDateTime.now(), conta, new BigDecimal("100.00"),
                TipoMovimentacao.DEPOSITAR, new BigDecimal("00"));

        MovimentacaoRecord movimentacaoRecord = new MovimentacaoRecord(1L, pessoa.getId(), LocalDateTime.now(),
                conta.getId(), new BigDecimal("100.00"), "DEPOSITAR");

        when(pessoaRepository.findAll()).thenReturn(List.of(pessoa));
        when(contaRepository.findAll()).thenReturn(List.of(conta));
        when(movimentacaoRepository.findAll()).thenReturn(List.of(movimentacao));

        when(movimentacaoMapper.toRecordList(List.of(movimentacao)))
                .thenReturn(List.of(movimentacaoRecord));

        when(movimentacaoService.calcularSaldo(pessoa.getId(), conta.getId()))
                .thenReturn(new BigDecimal("100.00"));

        List<PessoaComContasRecord> result = pessoaService.findAllPessoasWithContas();

        assertNotNull(result);
        assertEquals(1, result.size());

        PessoaComContasRecord pessoaComContas = result.getFirst();
        assertEquals(pessoa.getId(), pessoaComContas.pessoa().id());
        assertEquals(pessoa.getNome(), pessoaComContas.pessoa().nome());
        assertEquals(pessoa.getCpf(), pessoaComContas.pessoa().cpf());
        assertEquals(pessoa.getEndereco(), pessoaComContas.pessoa().endereco());

        assertEquals(1, pessoaComContas.contas().size());
        ContaComMovimentacao contaComMov = pessoaComContas.contas().getFirst();

        assertEquals(conta.getId(), contaComMov.id());
        assertEquals(conta.getNumeroConta(), contaComMov.numeroConta());
        assertEquals(new BigDecimal("100.00"), contaComMov.totalValor());

        assertEquals(1, contaComMov.movimentacoes().size());
        MovimentacaoRecord movRecord = contaComMov.movimentacoes().getFirst();
        assertEquals(movimentacaoRecord.id(), movRecord.id());
        assertEquals(movimentacaoRecord.conta(), movRecord.conta());
        assertEquals(movimentacaoRecord.valor(), movRecord.valor());
        assertEquals(movimentacaoRecord.data(), movRecord.data());

        verify(pessoaRepository, times(1)).findAll();
        verify(contaRepository, times(1)).findAll();
        verify(movimentacaoRepository, times(1)).findAll();
        verify(movimentacaoMapper, times(1)).toRecordList(List.of(movimentacao));
        verify(movimentacaoService, times(1)).calcularSaldo(pessoa.getId(), conta.getId());
    }
}