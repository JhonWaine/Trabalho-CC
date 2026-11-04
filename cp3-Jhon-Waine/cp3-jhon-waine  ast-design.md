# Projeto da Árvore de Sintaxe Abstrata (AST) e Visitors

## 1. Hierarquia de Nós da AST

Todos os nós herdam da classe abstrata `Node`, que armazena `line` (int) e `column` (int) referentes ao primeiro token da construção, além da assinatura `public abstract <T> T accept(Visitor<T> visitor)`. As classes de expressão herdam da subclasse abstrata `Expr extends Node`, e os comandos herdam de `Stmt extends Node`, facilitando a futura adição de atributos semânticos (como `Type` em `Expr` e ponteiros para a Tabela de Símbolos em `Id` e declarações) sem alterar a estrutura da árvore.

| Nó Concreto | Superclasse | Atributos (com tipo) | Produções da Gramática que o geram |
| :--- | :--- | :--- | :--- |
| **Program** | `Node` | `List<Node> declarations` | `Program -> DeclList EOF` |
| **VarDecl** | `Stmt` | `String type`, `String name`, `Expr init` (opcional/`null`) | `Decl -> Type id DeclTail` (quando `DeclTail -> VarInit ;`) e `Stmt -> Type id VarInit ;` |
| **FuncDecl** | `Node` | `String returnType`, `String name`, `List<Param> params`, `Block body` | `Decl -> Type id DeclTail` (quando `DeclTail -> ( ParamList ) Block`) |
| **Param** | `Node` | `String type`, `String name` | `ParamList -> Type id ParamListTail` e `ParamListTail -> , Type id ParamListTail` |
| **Block** | `Stmt` | `List<Stmt> statements` | `Block -> { StmtList }` |
| **Assign** | `Stmt` | `String name`, `Expr value` | `Stmt -> id AssignOrCall` (quando `AssignOrCall -> = Expr ;`) |
| **CallStmt** | `Stmt` | `String name`, `List<Expr> args` | `Stmt -> id AssignOrCall` (quando `AssignOrCall -> ( ArgList ) ;`) |
| **If** | `Stmt` | `Expr condition`, `Block thenBranch`, `Block elseBranch` (opcional/`null`) | `Stmt -> if ( Expr ) Block ElsePart` |
| **While** | `Stmt` | `Expr condition`, `Block body` | `Stmt -> while ( Expr ) Block` |
| **Return** | `Stmt` | `Expr value` (opcional/`null` para procedimentos `void`) | `Stmt -> return ReturnExpr ;` |
| **Bin** | `Expr` | `String op`, `Expr left`, `Expr right` | `RelTail -> RelOp Add RelTail`, `AddTail -> AddOp Term AddTail`, `TermTail -> MulOp Factor TermTail` |
| **Unary** | `Expr` | `String op`, `Expr expr` | `Factor -> - Factor` |
| **Call** | `Expr` | `String name`, `List<Expr> args` | `Base -> id BaseTail` (quando `BaseTail -> ( ArgList )`) |
| **Id** | `Expr` | `String name` | `Base -> id BaseTail` (quando `BaseTail -> ε`) |
| **IntLit** | `Expr` | `int value` | `Base -> int_val` |
| **FloatLit** | `Expr` | `double value` | `Base -> float_val` |
| **BoolLit** | `Expr` | `boolean value` | `Base -> true \| false` |
| **StringLit**| `Expr` | `String value` | `Base -> string_val` |
| **CharLit** | `Expr` | `char value` | Suporte estrutural na AST para literais `char` do escopo fixo |

---

## 2. Mapeamento da Gramática para a AST

Abaixo está o destino de cada não-terminal e símbolo terminal da gramática LL(1):

* **Viram nós diretamente:**
  * `Program` $\rightarrow$ `Program`
  * `Decl` $\rightarrow$ `VarDecl` ou `FuncDecl` (decidido por `DeclTail`)
  * `Block` $\rightarrow$ `Block`
  * `Stmt` $\rightarrow$ `VarDecl`, `Assign`, `CallStmt`, `If`, `While` ou `Return`
* **Viram atributos de outros nós:**
  * `Type` $\rightarrow$ Atributo `String type` / `returnType` em `VarDecl`, `FuncDecl` e `Param`.
  * `DeclList`, `StmtList`, `ParamList`, `ParamListTail`, `ArgList`, `ArgListTail` $\rightarrow$ Viram listas (`List<...>`) dentro de `Program`, `Block`, `FuncDecl`, `Call` e `CallStmt`.
  * `VarInit`, `ElsePart`, `ReturnExpr` $\rightarrow$ Viram atributos filhos opcionais (`init`, `elseBranch`, `value`) nos nós `VarDecl`, `If` e `Return`.
* **Não viram nó na AST (descartados após cumprirem seu papel sintático):**
  1. **Caso (a) — Não-terminais criados para precedência de operadores (`Expr`, `Rel`, `Add`, `Term`, `Factor`, `Base`):** Existem apenas para impor a hierarquia de precedência entre operadores relacionais, aditivos, multiplicativos e unários; na AST, colapsam diretamente no nó da operação (`Bin`, `Unary`) ou no operando folha (`Id`, literais).
  2. **Caso (b) — Não-terminais criados para eliminar a recursão à esquerda (`RelTail`, `AddTail`, `TermTail`):** Existem apenas para viabilizar a análise descendente LL(1); durante o parsing, eles recebem a subárvore esquerda por parâmetro e constroem nós `Bin` associativos à esquerda.
  3. **Caso (c) — Não-terminais criados pela fatoração à esquerda (`DeclTail`, `AssignOrCall`, `BaseTail`):** Existem apenas para adiar a decisão entre duas produções com mesmo prefixo (ex.: variável vs. função, atribuição vs. chamada); apenas completam a instância do nó principal.
  4. **Pontuação e delimitadores (`(`, `)`, `{`, `}`, `,`, `;`):** Não carregam semântica própria além de delimitar agrupamentos já representados pela própria estrutura hierárquica da árvore.

---

## 3. Registro de Posição

Todo nó concreto recebe `line` e `column` no construtor a partir do primeiro `Token` que iniciou aquela regra (ex.: a posição do token `if` para o nó `If`, a posição do operador para `Bin`, ou a posição do tipo para `VarDecl`/`FuncDecl`).

---

## 4. Especificação dos Visitors

### 4.1 Impressora da Árvore (`AstPrinter`)
Percorre a AST em pré-ordem produzindo uma linha por nó, indentada com **2 espaços por nível de profundidade**:
* `Program`
* `VarDecl(<type>, <name>)` (seguido do filho `init` indentado, se houver)
* `FuncDecl(<returnType>, <name>)` (seguido dos nós `Param` e do `Block` indentados)
* `Param(<type>, <name>)`
* `Block` (seguido dos comandos filhos indentados)
* `Assign(<name>)` (seguido da expressão indentada)
* `CallStmt(<name>)` (seguido dos argumentos indentados)
* `If` (seguido da condição, do bloco `then` e, se existir, do bloco `else`)
* `While` (seguido da condição e do bloco do corpo)
* `Return` (seguido da expressão de retorno indentada, se existir)
* `Bin(<op>)` (seguido do operando esquerdo e do operando direito indentados)
* `Unary(<op>)` (seguido do operando indentado)
* `Call(<name>)` (seguido dos argumentos indentados)
* `Id(<name>)`, `IntLit(<val>)`, `FloatLit(<val>)`, `BoolLit(<val>)`, `StringLit(<val>)`, `CharLit(<val>)`

### 4.2 Reimpressora de Código-Fonte (`SourceReprinter`)
Reconstrói o código-fonte em forma canônica:
* Expressões binárias são sempre envolvidas por parênteses `(<esq> <op> <dir>)` e unárias por `(-<expr>)`, preservando de maneira inequívoca a precedência e a associatividade da AST sem ambiguidade no *round-trip*.
* Blocos `{ ... }` utilizam indentação padronizada de 2 espaços.

---

## 5. Árvore Desenhada à Mão (Mais de 15 Nós) e Cortes da Derivação

**Programa de Exemplo:**
```c
int calcular(int a) {
  while (a < 10) {
    if (a == 5) {
      return a;
    }
    a = a + 1;
  }
  return a;
}