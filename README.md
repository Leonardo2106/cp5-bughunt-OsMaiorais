# Checkpoint 5 — Bug Hunt PetFiap

## Identificação

**Grupo:** Os Maiorais

| Integrante | RM | Turma |
|---|---|---|
| Carolina Monteiro Bernardo | 564651 | 2CCPW |
| Leonardo de Magalhães Piassa | 563663 | 2CCPW |

| Campo | |
|---|---|
| **Total de bugs corrigidos** | 10 / 12 |
| **Total de ajustes de Clean Code** | 3 / 6 |
| **Total de testes novos escritos** | 4 / 6 |
| **Suíte final (Run As → JUnit Test)** | 23 testes, 4 falhas (entrega parcial solicitada) |

---

## Parte 1 — Bugs encontrados

> Uma linha por bug, na ordem em que você os encontrou. Use a numeração dos seus
> commits (`fix: bug01 ...`). Preencha TODAS as colunas — metade da nota está aqui.

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | O Builder montava o atendimento com `petNome` nulo, mesmo após `comPet("Rex", ...)`. | `AtendimentoBuilder.java`, método `comPet`: o parâmetro era atribuído a ele mesmo (`petNome = petNome`). | Usei `this.petNome = petNome` para preencher o atributo da instância. | Encapsulamento, referência `this` e padrão Builder. |
| bug02 | Ao pedir uma `TOSA`, a Factory devolvia uma instância de `Banho`. | `AtendimentoFactory.java`, ramo `TOSA` do `switch`: a subclasse concreta estava trocada. | O ramo `TOSA` passou a instanciar `Tosa`. | Factory Method, abstração e polimorfismo. |
| bug03 | A consulta criada pela Factory perdia protocolo, pet, porte, tutor, data e status. | `ConsultaVeterinaria.java`, construtor: era chamado `super()` em vez do construtor completo da superclasse. | Encaminhei todos os parâmetros para `super(...)`. | Herança, construtores e estado válido do objeto. |
| bug04 | Chamadas a `getInstancia()` devolviam objetos diferentes e a sequência reiniciava em 1. | `GeradorProtocolo.java`: a instância criada nunca era armazenada; o contador também não era protegido contra concorrência. | Criei uma instância única, estática e final, e sincronizei `proximo()`. | Singleton, estado compartilhado e segurança de threads. |
| bug05 | O teste novo de preços do banho esperava R$ 60 para porte pequeno, mas recebeu R$ 100; o porte grande recebia R$ 60. | `Banho.java`, método `calcularPreco`: os valores dos portes pequeno e grande estavam invertidos. | Corrigi o mapeamento para pequeno = 60, médio = 80 e grande = 100. | Polimorfismo de sobrescrita e regra de negócio no model. |
| bug06 | O teste novo chamou `getDuracaoMinutos()` por uma referência `Atendimento` e recebeu 30, não 60. | `Tosa.java`: `getDuracaoMinutos(String porte)` era uma sobrecarga, não a sobrescrita do método sem parâmetros. | Removi o parâmetro e adicionei `@Override`, fazendo a chamada polimórfica retornar 60. | Sobrescrita versus sobrecarga e anotação `@Override` (Aula 7). |
| bug07 | O agendamento duplicado do mesmo pet no mesmo horário passava pela verificação de conflito e era salvo; no teste, o `save` era chamado e o resultado nulo gerava `NullPointerException` em vez de `HorarioOcupadoException`. | `AgendaService.java`, método `agendar`: `getPetNome()` e `getDataHora()` eram comparados com `==`, que compara referências e não valores (um `LocalDateTime` vindo de outra requisição é outro objeto). | Troquei `==` por `.equals()` nas duas comparações, mantendo a condição de status `AGENDADO`. | `==` vs `.equals()` e referências de objetos (Aula 7). |
| bug08 | Buscar um id inexistente retornava `null` em vez de lançar `AtendimentoNaoEncontradoException`; `concluir` e `cancelar` dariam `NullPointerException` e o controller nunca devolveria 404. | `AgendaService.java`, método `buscarPorId`: o `catch (Exception e) { return null; }` capturava a exceção lançada pelo `orElseThrow` e a transformava em `null`. | Removi o `try/catch` genérico; a exceção customizada agora chega a quem chamou (e o controller a converte em 404). | Tratamento de exceções: não engolir exceções com `catch` genérico e exceções unchecked customizadas (Aula 11). |
| bug09 | O Builder aceitava montar um atendimento sem nome do pet ou sem porte, gerando objetos inválidos (os testes esperavam `IllegalArgumentException` e nada era lançado). | `AtendimentoBuilder.java`, método `construir`: não havia validação; o comentário delegava a regra ao controller, que também não validava. | Adicionei a validação no `construir()`: nome e porte nulos ou em branco lançam `IllegalArgumentException` com mensagem clara; o controller já converte isso em 400. | Padrão Builder (validação na construção), encapsulamento e estado válido do objeto (Aulas 3, 4 e 14). |
| bug10 | Um atendimento já CONCLUIDO (ou já CANCELADO) podia ser cancelado, voltando para `CANCELADO` sem erro; o teste04 esperava `StatusInvalidoException`. | `Atendimento.java`, método `cancelar`: atribuía `status = "CANCELADO"` sem verificar o status atual, ao contrário de `concluir()`. | `cancelar()` agora só aceita `AGENDADO` e lança `StatusInvalidoException` nos demais casos, como já fazia `concluir()`; o controller responde 409. | Encapsulamento das regras de transição de status no model e exceções customizadas (Aulas 3 e 11). |
| bug11 | | | | |
| bug12 | | | | |

## Parte 2 — Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que eu mudei |
|---|---|---|---|
| clean01 | `AtendimentoFactory.criar` | Parâmetros de uma letra (`p`, `t`, `n`, `po`, `tu`, `d`) escondiam a intenção do código. | Renomeei os parâmetros para `protocolo`, `tipo`, `petNome`, `petPorte`, `tutorNome` e `dataHora`. |
| clean02 | Construtor de `GeradorProtocolo` | A classe de domínio produzia efeito colateral com `System.out.println`, misturando regra de negócio e saída de console. | Removi a impressão; a criação do Singleton agora apenas inicializa seu estado. |
| clean03 | Final de `AtendimentoController` | Código especulativo e método privado nunca usado aumentavam o ruído e sugeriam uma regra de desconto ainda não aprovada. | Removi o comentário de funcionalidade futura e `calcularDescontoFidelidade`, mantendo apenas responsabilidades atuais do controller. |
| clean04 | | | |
| clean05 | | | |
| clean06 | | | |

## Parte 3 — Testes novos (regras que estavam sem cobertura)

> Uma linha por teste novo (`test: ...`). "Regra coberta" é o comportamento do
> contrato (seção 3 do enunciado) que o teste protege. Em "Resultado", diga se o
> teste ficou vermelho ao ser escrito (revelou bug — qual?) ou verde de cara
> (regra já estava correta).

| # | Teste escrito (classe.método) | Regra coberta | Resultado ao escrever (vermelho/verde) |
|---|---|---|---|
| teste01 | `RegrasSemCoberturaTest.deveCalcularPrecoPorPorteQuandoAtendimentoForBanho` | Banho custa R$ 60, R$ 80 e R$ 100 para portes pequeno, médio e grande. | Vermelho: revelou o bug05, pois pequeno retornava R$ 100 e grande retornava R$ 60. |
| teste02 | `RegrasSemCoberturaTest.deveDurar60MinutosQuandoAtendimentoForTosa` | Tosa dura 60 minutos mesmo quando referenciada pelo tipo abstrato `Atendimento`. | Vermelho: revelou o bug06; a chamada polimórfica usava os 30 minutos da superclasse. |
| teste03 | `RegrasSemCoberturaTest.deveManterPrecoFixoQuandoConsultaTiverQualquerPorte` | Consulta veterinária custa R$ 150 para qualquer porte. | Verde de cara: a regra de preço fixo já estava implementada corretamente. |
| teste04 | `RegrasSemCoberturaTest.deveRecusarCancelamentoQuandoAtendimentoJaEstiverConcluido` | `cancelar()` em atendimento `CONCLUIDO` deve ser recusado com `StatusInvalidoException` (atendimento já realizado) e manter o status. | Vermelho: revelou o bug10; o `cancelar()` mudava o status para `CANCELADO` sem verificar o estado atual. |
| teste05 | | | |
| teste06 | | | |

---

## Parte 4 — Perguntas de reflexão

> Responda com suas palavras, 5 a 10 linhas cada, **usando o código real do
> projeto como exemplo**. Respostas genéricas de tutorial não pontuam.

### 1. A suíte como contrato (Aula 15)
O projeto chegou com 20 testes, 9 vermelhos. Descreva como você usou as
mensagens de falha (ex.: `expected: <Rex> but was: <null>`) para caçar os bugs.
O que a suíte de testes tem de melhor do que testar tudo na mão com curl?

Comecei pelas mensagens objetivas da suíte e relacionei cada valor recebido ao ponto onde o objeto era montado.
O `expected: <Rex> but was: <null>` levou ao `petNome = petNome` do Builder, que não alterava o atributo.
O tipo concreto errado apontou para o ramo `TOSA` da Factory e a sequência reiniciada apontou para o Singleton.
Os testes repetem os mesmos cenários de forma rápida e determinística, sem depender de servidor, banco ou requisições manuais.
Com `curl`, o diagnóstico seria mais lento e seria fácil confundir falha de infraestrutura com falha de regra de negócio.

### 2. Mock e injeção de dependência (Aulas 13 a 15)
No `AgendaServiceTest`, o `@Mock` cria um `AtendimentoRepository` falso e o
`@InjectMocks` o injeta no service. Explique a relação disso com o `@Autowired`
que o Spring faz em produção — quem "injeta" em cada mundo, e por que o teste
consegue rodar sem banco e sem subir o Spring?

Em produção, o Spring encontra `AgendaService` por `@Service` e injeta uma implementação de `AtendimentoRepository` pelo `@Autowired`.
No teste, a extensão do Mockito processa `@Mock` e cria um substituto controlável para o repositório.
Depois, `@InjectMocks` coloca esse objeto falso no `AgendaService`, ocupando o papel que seria do container.
Os `when(...)` definem as respostas necessárias e `verify(...)` confirma se a colaboração ocorreu como esperado.
Assim, a lógica do serviço é testada sem abrir conexão Oracle e sem inicializar o contexto do Spring.

### 3. `==` vs `.equals()` (Aula 7)
Um dos bugs fazia o agendamento duplicado passar pela verificação de conflito.
Explique por que `==` entre Strings e `LocalDateTime` falhou aqui, por que ele
"funciona por sorte" com literais como `"Rex"`, e o que a sua correção mudou.

No código recebido, `==` compara se duas referências apontam para o mesmo objeto, não se os valores são equivalentes.
Strings literais podem compartilhar a referência pelo pool da JVM, criando a impressão de que a comparação funciona.
Uma String ou `LocalDateTime` reconstruído pela requisição pode ter o mesmo valor em outra instância, deixando o conflito passar.
Esse item não integra os seis bugs corrigidos nesta entrega parcial e permanece visível na suíte final.
A correção prevista é comparar os valores com `.equals()`, mantendo também a condição de status `AGENDADO`.

### 4. Sobrescrita vs sobrecarga (Aula 7)
Um dos bugs compilava sem nenhum erro: um método parecia sobrescrever
`getDuracaoMinutos`, mas na verdade criava uma assinatura nova. Explique a
diferença entre override e overload nesse caso e por que a anotação `@Override`
teria impedido o bug.

`Atendimento` declara `getDuracaoMinutos()` sem parâmetros, retornando 30 por padrão.
`Tosa` possuía `getDuracaoMinutos(String porte)`, portanto criava uma sobrecarga com assinatura diferente.
Quando o objeto era acessado como `Atendimento`, o método herdado continuava sendo chamado e devolvia 30.
A correção removeu o parâmetro e adicionou `@Override`, fazendo a tosa fornecer seus 60 minutos polimorficamente.
Se a anotação existisse desde o início, o compilador teria rejeitado a assinatura incompatível.

### 5. Singleton manual vs bean do Spring (Aula 14)
O `GeradorProtocolo` é um Singleton escrito à mão e causou um dos bugs.
Explique o que ele garante, qual foi o bug, e por que o `AgendaService`
(`@Service`) não corre o mesmo risco no container do Spring.

O `GeradorProtocolo` precisa manter uma única instância para todos os atendimentos compartilharem o contador.
O método original criava um objeto quando o campo era nulo, mas não o armazenava, reiniciando a sequência.
Passei a usar uma instância `static final` e sincronizei `proximo()` para proteger incrementos concorrentes.
Já o `AgendaService` é descoberto por `@Service` e o container administra, por padrão, uma única instância desse bean.
O Singleton manual depende da implementação Java correta; o ciclo de vida do serviço é responsabilidade do Spring.

### 6. Cobertura de testes: onde parar? (Aula 15)
Dos 6 testes novos que você escreveu, alguns ficaram vermelhos (revelaram
bugs) e outros verdes de cara (regras já corretas). Vale a pena manter os que
ficaram verdes? Em um projeto real com prazo, o que você priorizaria testar:
caminho feliz, caminhos de erro, ou 100% de cobertura? Justifique.

Nesta entrega parcial foram escritos três dos seis testes: dois nasceram vermelhos e um nasceu verde.
O teste verde da consulta continua valioso porque protege a regra de preço fixo contra regressões futuras.
Em um projeto real, eu começaria pelas regras de maior impacto e pelos erros que evitam dados inválidos ou perdas.
Também manteria ao menos um caminho feliz por fluxo principal, comprovando que as partes colaboram corretamente.
Cobertura de 100% é um indicador, não um objetivo isolado: testes relevantes valem mais que linhas exercitadas sem boas asserções.

---

## Parte 5 — Espaço livre (opcional)

Alguma dificuldade, dúvida ou comentário sobre o checkpoint?

Foram implementados exatamente metade dos itens solicitados em cada categoria, conforme o escopo desta entrega.
