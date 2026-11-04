# Log de Uso de Inteligência Artificial

**Disciplina:** Construção de Compiladores  
**Instituição:** Universidade Federal de Uberlândia (UFU)  
**Aluno:** Jhon Waine Mendes Gonçalves  
**Matrícula:** 12021BCC028  
**Checkpoint:** 3 — Árvore de Sintaxe Abstrata (AST) e Visitor  

---

Conforme exigido nas diretrizes do projeto, este documento registra o uso de ferramentas de Inteligência Artificial (Gemini) durante o desenvolvimento deste entregável. O uso limitou-se à geração de código repetitivo (*boilerplate*) de classes de dados e formatação de documentação.

### 1. Projeto da Hierarquia da AST (`ast-design.md`)
* **Uso:** Formatação de tabelas Markdown e diagramação em texto (ASCII) da árvore.
* **Detalhes:** As decisões de quais não-terminais viram nós e quais são descartados (não-terminais de precedência, recursão à esquerda e fatoração) foram tomadas com base nos conceitos da Aula 7. A IA foi utilizada para organizar a tabela de atributos e formatar visualmente a árvore de 22 nós no arquivo `ast-design.md`.

### 2. Classes de Nós e Infraestrutura Visitor (`src/ast/` e `src/visitor/`)
* **Uso:** Geração de *boilerplate* de construtores, *getters* e métodos `accept`.
* **Detalhes:** Como a hierarquia da AST possui 19 classes concretas que seguem exatamente o mesmo padrão estrutural (campos `final`, construtor repassando `line` e `column` para `Node` e método `accept` invocando `visitor.visitX(this)`), a IA foi utilizada para gerar esse código repetitivo sem uso de `instanceof` ou `switch`.

### 3. Refatoração do Parser e Suíte de Testes
* **Uso:** Auxílio na revisão de sintaxe para passagem de subárvores herdadas e leitura de arquivos em Java.
* **Detalhes:** O `Scanner` do CP1 foi mantido sem alterações. A IA foi consultada para revisar a assinatura dos métodos `parseRelTail`, `parseAddTail` e `parseTermTail` recebendo o nó esquerdo (`Expr left`) para garantir associatividade à esquerda, bem como para gerar o código *boilerplate* de leitura dos arquivos de teste (`Files.readAllBytes`) na classe `ParserAstTest`.

---
*Declaro que toda a arquitetura da AST, o mecanismo de double dispatch do padrão Visitor e a construção recursiva dos nós no Parser são compreendidos integralmente e podem ser explicados em detalhes durante a arguição oral.*