# Gramática, FIRST/FOLLOW e Verificação LL(1)

## 1. Gramática Livre de Contexto (Fatorada e sem Recursão à Esquerda)
Para garantir que o parser recursivo descendente funcione sem retrocesso (backtracking) e sem loops infinitos, a gramática original foi ajustada. Declarações de variáveis e funções foram fatoradas à esquerda (`DeclTail`), e as expressões foram fatoradas para remover a recursão à esquerda (`ExprTail`, `BaseTail`).

**Terminais:** `int`, `double`, `bool`, `char`, `string`, `void`, `if`, `else`, `while`, `return`, `true`, `false`, `id`, `int_val`, `float_val`, `string_val`, `op`, `(`, `)`, `{`, `}`, `,`, `;`

**Regras de Produção:**
1.  `Program -> DeclList EOF`
2.  `DeclList -> Decl DeclList | ε`
3.  `Decl -> Type id DeclTail`
4.  `DeclTail -> ; | ( ParamList ) Block`
5.  `Type -> int | double | bool | char | string | void`
6.  `ParamList -> Type id ParamListTail | ε`
7.  `ParamListTail -> , Type id ParamListTail | ε`
8.  `Block -> { StmtList }`
9.  `StmtList -> Stmt StmtList | ε`
10. `Stmt -> Type id ; | id AssignOrCall | if ( Expr ) Block ElsePart | while ( Expr ) Block | return ReturnExpr ;`
11. `AssignOrCall -> = Expr ; | ( ArgList ) ;`
12. `ElsePart -> else Block | ε`
13. `ReturnExpr -> Expr | ε`
14. `ArgList -> Expr ArgListTail | ε`
15. `ArgListTail -> , Expr ArgListTail | ε`
16. `Expr -> Base ExprTail`
17. `ExprTail -> op Base ExprTail | ε`
18. `Base -> id BaseTail | int_val | float_val | string_val | true | false | ( Expr )`
19. `BaseTail -> ( ArgList ) | ε`

---

## 2. Tabela de Conjuntos FIRST e FOLLOW

| Não-Terminal | FIRST | FOLLOW |
| :--- | :--- | :--- |
| **Program** | `{int, double, bool, char, string, void, ε}` | `{$}` |
| **DeclList** | `{int, double, bool, char, string, void, ε}` | `{$}` |
| **Decl** | `{int, double, bool, char, string, void}` | `{int, double, bool, char, string, void, $}` |
| **DeclTail** | `{;, (}` | `{int, double, bool, char, string, void, $}` |
| **Type** | `{int, double, bool, char, string, void}` | `{id}` |
| **ParamList** | `{int, double, bool, char, string, void, ε}` | `{)}` |
| **ParamListTail** | `{,, ε}` | `{)}` |
| **Block** | `{{}` | `{int, double, bool, char, string, void, $, id, if, while, return, }, else}` |
| **StmtList** | `{int, double, bool, char, string, void, id, if, while, return, ε}` | `{}}` |
| **Stmt** | `{int, double, bool, char, string, void, id, if, while, return}` | `{int, double, bool, char, string, void, id, if, while, return, }}` |
| **AssignOrCall**| `{=, (}` | `{int, double, bool, char, string, void, id, if, while, return, }}` |
| **ElsePart** | `{else, ε}` | `{int, double, bool, char, string, void, id, if, while, return, }}` |
| **ReturnExpr** | `{id, int_val, float_val, string_val, true, false, (, ε}` | `{;}` |
| **ArgList** | `{id, int_val, float_val, string_val, true, false, (, ε}` | `{)}` |
| **ArgListTail** | `{,, ε}` | `{)}` |
| **Expr** | `{id, int_val, float_val, string_val, true, false, (}` | `{;, ), ,}` |
| **ExprTail** | `{op, ε}` | `{;, ), ,}` |
| **Base** | `{id, int_val, float_val, string_val, true, false, (}` | `{op, ;, ), ,}` |
| **BaseTail** | `{(, ε}` | `{op, ;, ), ,}` |

---

## 3. Verificação Explícita da Condição LL(1)

Uma gramática é LL(1) se e somente se, para todas as produções $A \rightarrow \alpha \mid \beta$:
1. $FIRST(\alpha) \cap FIRST(\beta) = \emptyset$
2. No máximo uma alternativa deriva vazio ($\epsilon$).
3. Se $\beta \rightarrow \epsilon$, então $FIRST(\alpha) \cap FOLLOW(A) = \emptyset$.

**Análise não-terminal por não-terminal (apenas os que têm mais de uma alternativa):**

*   **DeclList $\rightarrow$ Decl DeclList | $\epsilon$**
    *   $FIRST(\text{Decl DeclList}) = \{int, double, bool, char, string, void\}$
    *   Alternativa anulável existe ($\epsilon$).
    *   $FOLLOW(\text{DeclList}) = \{\$\}$. Interseção vazia com FIRST. **(LL1 OK)**
*   **DeclTail $\rightarrow$ ; | ( ParamList ) Block**
    *   $FIRST(;) = \{;\}$ e $FIRST((...) = \{(\}$. Interseção vazia. Nenhuma é anulável. **(LL1 OK)**
*   **ParamList $\rightarrow$ Type id ParamListTail | $\epsilon$**
    *   $FIRST(\text{Type}) = \{int, double...\}$
    *   $FOLLOW(\text{ParamList}) = \{)\}$. Interseção vazia. **(LL1 OK)**
*   **ParamListTail $\rightarrow$ , Type id ParamListTail | $\epsilon$**
    *   $FIRST(,) = \{,\}$. $FOLLOW(\text{ParamListTail}) = \{)\}$. Interseção vazia. **(LL1 OK)**
*   **StmtList $\rightarrow$ Stmt StmtList | $\epsilon$**
    *   $FIRST(\text{Stmt}) = \{int, double..., id, if, while, return\}$
    *   $FOLLOW(\text{StmtList}) = \{\}\}$. Interseção vazia. **(LL1 OK)**
*   **AssignOrCall $\rightarrow$ = Expr ; | ( ArgList ) ;**
    *   $FIRST(=) = \{=\}$ e $FIRST((...) = \{(\}$. Interseção vazia. **(LL1 OK)**
*   **ElsePart $\rightarrow$ else Block | $\epsilon$**
    *   $FIRST(else) = \{else\}$. $FOLLOW(\text{ElsePart}) = \text{FIRST}(Stmt) \cup \{\}\}$. Interseção vazia. **(LL1 OK)**
*   **ReturnExpr $\rightarrow$ Expr | $\epsilon$**
    *   $FIRST(\text{Expr}) = \{id, int\_val...\}$. $FOLLOW(\text{ReturnExpr}) = \{;\}$. Interseção vazia. **(LL1 OK)**
*   **ArgList $\rightarrow$ Expr ArgListTail | $\epsilon$** e **ArgListTail $\rightarrow$ , Expr ArgListTail | $\epsilon$**
    *   Regra análoga a ParamList. FIRSTs e FOLLOWs disjointos entre vírgulas e parênteses de fechamento. **(LL1 OK)**
*   **ExprTail $\rightarrow$ op Base ExprTail | $\epsilon$**
    *   $FIRST(\text{op}) = \{op\}$. $FOLLOW(\text{ExprTail}) = \{;, ), ,\}$. Interseção vazia. **(LL1 OK)**
*   **BaseTail $\rightarrow$ ( ArgList ) | $\epsilon$**
    *   $FIRST(() = \{(\}$. $FOLLOW(\text{BaseTail}) = \{op, ;, ), ,\}$. Interseção vazia. **(LL1 OK)**