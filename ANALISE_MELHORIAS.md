# 📊 Análise de Melhorias - Gestão Bancária

Este documento apresenta uma análise detalhada do projeto de Gestão Bancária, identificando melhorias para a API (Spring Boot) e para o App (Next.js), além de sugestões de novas funcionalidades.

---

## 📋 Índice

1. [Melhorias na API (Backend)](#-melhorias-na-api-backend)
2. [Melhorias no App (Frontend)](#-melhorias-no-app-frontend)
3. [Novas Funcionalidades Sugeridas](#-novas-funcionalidades-sugeridas)
4. [Melhorias de Infraestrutura](#-melhorias-de-infraestrutura)
5. [Melhorias de Segurança](#-melhorias-de-segurança)
6. [Priorização das Melhorias](#-priorização-das-melhorias)

---

## 🔧 Melhorias na API (Backend)

### 1. Tratamento Global de Exceções

**Problema:** As exceções customizadas (`Exception400`, `Exception404`, `Exception409`) usam apenas `@ResponseStatus`, mas não há um `@ControllerAdvice` para padronizar as respostas de erro.

**Solução:** Criar um `GlobalExceptionHandler` para padronizar o formato das respostas de erro.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception400.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception400 ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage
            ));
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("Erro de validação", 400, errors));
    }
}
```

---

### 2. Paginação nas Listagens

**Problema:** Os endpoints `getAll()` retornam listas completas, o que pode causar problemas de performance com grandes volumes de dados.

**Solução:** Implementar paginação usando `Pageable` do Spring Data.

```java
@GetMapping
public ResponseEntity<Page<PessoaRecord>> getAll(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "nome") String sortBy) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    return ResponseEntity.ok(pessoaService.findAllPerson(pageable));
}
```

---

### 3. Nomenclatura de Métodos nos Controllers

**Problema:** Vários métodos nos controllers têm o mesmo nome `getAll()` para operações diferentes (DELETE, PUT).

**Solução:** Renomear os métodos para refletir sua funcionalidade:

| Atual | Sugerido |
|-------|----------|
| `getAll(@PathVariable Long id)` (DELETE) | `delete(@PathVariable Long id)` |
| `getAll(@PathVariable Long id, ...)` (PUT) | `update(@PathVariable Long id, ...)` |

---

### 4. DTOs de Request/Response Separados

**Problema:** O mesmo record (`PessoaRecord`, `ContaRecord`) é usado tanto para request quanto para response, expondo campos desnecessários.

**Solução:** Criar DTOs específicos:

```java
// Request
public record PessoaCreateRequest(
    @NotBlank String nome,
    @NotBlank String cpf,
    @NotBlank String endereco
) {}

// Response
public record PessoaResponse(
    Long id,
    String nome,
    String cpf,
    String endereco,
    LocalDateTime createdAt
) {}
```

---

### 5. Auditoria de Entidades

**Problema:** As entidades não possuem campos de auditoria (`createdAt`, `updatedAt`, `createdBy`).

**Solução:** Criar uma classe base com auditoria:

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

---

### 6. Validação de CPF Duplicada

**Problema:** A validação de CPF está na entidade (`@CPF`) e também é tratada via exceção no service. Isso é redundante e pode gerar mensagens de erro inconsistentes.

**Solução:** Centralizar a validação no service com uma mensagem clara ou criar um validator customizado.

---

### 7. Cache para Consultas Frequentes

**Problema:** Consultas como `findAllPessoasWithContas()` fazem múltiplas queries ao banco.

**Solução:** Implementar cache com Spring Cache:

```java
@Cacheable(value = "pessoasComContas", key = "'all'")
public List<PessoaComContasRecord> findAllPessoasWithContas() { ... }

@CacheEvict(value = "pessoasComContas", allEntries = true)
public Pessoa createPerson(Pessoa entity) { ... }
```

---

### 8. Soft Delete para Entidades

**Problema:** O delete é físico, perdendo o histórico de dados.

**Solução:** Implementar soft delete:

```java
@Entity
@SQLDelete(sql = "UPDATE pessoas SET deleted = true WHERE id_pessoa = ?")
@Where(clause = "deleted = false")
public class Pessoa {
    @Column(name = "deleted")
    private boolean deleted = false;
}
```

---

### 9. Query Otimizada para Saldo

**Problema:** O cálculo de saldo busca todas as movimentações e processa em memória.

**Solução:** Usar query nativa ou JPQL:

```java
@Query("SELECT COALESCE(SUM(CASE WHEN m.tipo = 'DEPOSITAR' THEN m.valor ELSE -m.valor END), 0) " +
       "FROM Movimentacoes m WHERE m.pessoa.id = :pessoaId AND m.conta.id = :contaId")
BigDecimal calcularSaldo(@Param("pessoaId") Long pessoaId, @Param("contaId") Long contaId);
```

---

### 10. Testes Unitários e de Integração

**Problema:** Não foram identificados testes no projeto.

**Solução:** Implementar testes:

```java
@SpringBootTest
@AutoConfigureMockMvc
class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarPessoaComSucesso() throws Exception {
        String json = """
            {"nome": "João Silva", "cpf": "12345678901", "endereco": "Rua A"}
            """;
        
        mockMvc.perform(post("/pessoa/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated());
    }
}
```

---

## 🎨 Melhorias no App (Frontend)

### 1. Gerenciamento de Estado Global

**Problema:** O estado é gerenciado localmente em cada página com `useState`, causando re-fetching desnecessário.

**Solução:** Implementar React Query (TanStack Query) ou Zustand:

```typescript
// Com React Query
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

export function usePessoas() {
  return useQuery({
    queryKey: ['pessoas'],
    queryFn: () => pessoaApi.getAll().then(r => r.data),
    staleTime: 5 * 60 * 1000, // 5 minutos
  });
}

export function useCreatePessoa() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: pessoaApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pessoas'] });
    },
  });
}
```

---

### 2. Componentização Excessiva de Páginas

**Problema:** As páginas têm muita lógica, misturando UI, estado e requisições.

**Solução:** Separar em hooks customizados e componentes menores:

```
/pessoa
  ├── page.tsx           # Layout da página
  ├── PessoaForm.tsx     # Formulário isolado
  ├── PessoaTable.tsx    # Tabela isolada
  └── usePessoa.ts       # Hook com lógica de negócio
```

---

### 3. Tratamento de Loading States

**Problema:** O loading é um simples boolean que desabilita campos.

**Solução:** Implementar estados de loading mais granulares e skeletons:

```typescript
const { data, isLoading, isFetching, error } = usePessoas();

if (isLoading) return <TableSkeleton rows={5} />;
if (error) return <ErrorState message={error.message} onRetry={refetch} />;
```

---

### 4. Validação de CPF no Frontend

**Problema:** A validação de CPF é apenas por tamanho/máscara.

**Solução:** Adicionar validação de dígitos verificadores no schema Zod:

```typescript
import { cpf } from 'cpf-cnpj-validator';

const pessoaSchema = z.object({
  cpf: z.string()
    .transform(v => v.replace(/\D/g, ''))
    .refine(cpf.isValid, { message: 'CPF inválido' }),
});
```

---

### 5. Feedback Visual de Ações

**Problema:** Os toasts são o único feedback. Faltam indicadores de loading em botões.

**Solução:** Adicionar loading states nos botões:

```tsx
<LoadingButton
  loading={isSubmitting}
  loadingPosition="start"
  startIcon={<SaveIcon />}
  variant="contained"
  type="submit"
>
  {isSubmitting ? 'Salvando...' : 'Salvar'}
</LoadingButton>
```

---

### 6. Confirmação de Ações Destrutivas

**Problema:** O delete não pede confirmação do usuário.

**Solução:** Implementar dialog de confirmação:

```tsx
const [deleteDialog, setDeleteDialog] = useState<{ open: boolean; id: string | null }>({
  open: false,
  id: null,
});

<ConfirmDialog
  open={deleteDialog.open}
  title="Confirmar exclusão"
  message="Tem certeza que deseja excluir esta pessoa? Esta ação não pode ser desfeita."
  onConfirm={() => handleDelete(deleteDialog.id)}
  onCancel={() => setDeleteDialog({ open: false, id: null })}
/>
```

---

### 7. Acessibilidade (a11y)

**Problema:** Faltam labels associados, aria-labels e focus management.

**Solução:**
- Adicionar `aria-label` em botões de ícone
- Implementar focus trap em modais
- Adicionar skip links para navegação
- Usar roles ARIA apropriados

```tsx
<IconButton
  aria-label="Excluir pessoa"
  onClick={handleDelete}
>
  <DeleteIcon />
</IconButton>
```

---

### 8. Responsividade

**Problema:** As tabelas podem não funcionar bem em dispositivos móveis.

**Solução:** Usar componentes responsivos do MUI ou implementar card view para mobile:

```tsx
const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

return isMobile ? (
  <PessoaCardList data={pessoas} />
) : (
  <PessoaTable data={pessoas} />
);
```

---

### 9. Internacionalização (i18n)

**Problema:** Textos hardcoded em português.

**Solução:** Implementar next-intl ou react-i18next para suportar múltiplos idiomas futuramente.

---

### 10. Tema e Design System

**Problema:** Estilos misturados entre Tailwind e MUI inline.

**Solução:** Criar um tema customizado do MUI e usar consistentemente:

```typescript
// theme.ts
import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: { main: '#2563eb' },
    secondary: { main: '#7c3aed' },
  },
  components: {
    MuiButton: {
      defaultProps: { variant: 'contained' },
    },
  },
});
```

---

## 🚀 Novas Funcionalidades Sugeridas

### Alta Prioridade

| Funcionalidade | Descrição | Complexidade |
|----------------|-----------|--------------|
| **Autenticação/Autorização** | Login, registro, JWT, refresh token | Alta |
| **Transferência entre contas** | Transferir valores entre contas do sistema | Média |
| **Extrato em PDF** | Gerar extrato de movimentações em PDF | Média |
| **Dashboard** | Visão geral com gráficos de saldos e movimentações | Média |

### Média Prioridade

| Funcionalidade | Descrição | Complexidade |
|----------------|-----------|--------------|
| **Notificações** | Alertas de movimentações por email/push | Média |
| **Tipos de conta** | Conta corrente, poupança, investimento | Baixa |
| **Categorização de movimentações** | Categorias como "Alimentação", "Transporte" | Baixa |
| **Busca e filtros avançados** | Filtrar por data, valor, tipo | Baixa |
| **Agendamento de movimentações** | Agendar depósitos/saques futuros | Média |

### Baixa Prioridade (Nice to Have)

| Funcionalidade | Descrição | Complexidade |
|----------------|-----------|--------------|
| **Multi-tenancy** | Suporte a múltiplas organizações | Alta |
| **PIX simulado** | Simulação de transferências PIX | Alta |
| **Cartão virtual** | Geração de cartões virtuais | Alta |
| **Relatórios financeiros** | Relatórios mensais/anuais | Média |
| **Modo escuro** | Tema dark no frontend | Baixa |
| **PWA** | App installável no celular | Baixa |

---

## 🏗️ Melhorias de Infraestrutura

### 1. Profiles do Spring

**Problema:** Apenas um profile (`docker`) foi identificado.

**Solução:** Criar profiles para diferentes ambientes:

```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/gestaoBancaria_dev
  jpa:
    show-sql: true

# application-prod.yml
spring:
  datasource:
    url: ${DATABASE_URL}
  jpa:
    show-sql: false
```

---

### 2. Health Checks

**Solução:** Adicionar Spring Actuator:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when_authorized
```

---

### 3. Containerização Melhorada

**Problema:** Dockerfile básico sem otimizações.

**Solução:** Dockerfile multi-stage:

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

### 4. CI/CD Pipeline

**Sugestão:** GitHub Actions para:
- Build e testes automatizados
- Análise de código (SonarQube)
- Deploy automático

```yaml
# .github/workflows/ci.yml
name: CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - run: mvn verify
```

---

### 5. Logging Estruturado

**Solução:** Configurar logs em JSON para melhor análise:

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>
</configuration>
```

---

## 🔒 Melhorias de Segurança

### 1. Autenticação e Autorização

**Solução:** Implementar Spring Security com JWT:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

---

### 2. Rate Limiting

**Solução:** Implementar rate limiting com Bucket4j ou no API Gateway:

```java
@Bean
public Bucket createBucket() {
    return Bucket.builder()
        .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
        .build();
}
```

---

### 3. CORS Restritivo

**Problema:** CORS permite todas as origens em desenvolvimento.

**Solução:** Configurar por ambiente:

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins(allowedOrigins) // Injetado via properties
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowCredentials(true);
}
```

---

### 4. Proteção contra Ataques

- **SQL Injection:** Já protegido pelo JPA/Hibernate
- **XSS:** Sanitizar inputs no frontend
- **CSRF:** Implementar tokens CSRF se usar sessões
- **Sensitive Data Exposure:** Não logar dados sensíveis

---

## 📊 Priorização das Melhorias

### 🔴 Crítico (Fazer Primeiro)

1. Global Exception Handler
2. Testes unitários básicos
3. Paginação nas listagens
4. Autenticação (se for para produção)

### 🟡 Importante (Segunda Fase)

1. DTOs separados (Request/Response)
2. Cache para consultas frequentes
3. React Query no frontend
4. Confirmação de delete
5. Componentização do frontend

### 🟢 Melhorias (Terceira Fase)

1. Auditoria de entidades
2. Soft delete
3. Dashboard com gráficos
4. Transferências entre contas
5. Extrato em PDF

### 🔵 Nice to Have

1. Internacionalização
2. Modo escuro
3. PWA
4. Notificações
5. Agendamento de movimentações

---

## 📝 Conclusão

O projeto Gestão Bancária possui uma boa base técnica com tecnologias modernas (Spring Boot 3.5, Next.js 15, React 19). As principais áreas de melhoria são:

1. **Backend:** Padronização de respostas, paginação e testes
2. **Frontend:** Gerenciamento de estado e componentização
3. **Segurança:** Implementar autenticação antes de produção
4. **Infraestrutura:** CI/CD e observabilidade

A implementação gradual dessas melhorias tornará o sistema mais robusto, escalável e fácil de manter.

---

*Documento gerado em: Agosto de 2026*
*Versão do projeto analisada: 0.0.1-SNAPSHOT*
