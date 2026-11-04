
# Trab CC:  Jhon Waine Mendes Gonçalves

## 1. Regras Gerais e Alfabeto

Esta seção detalha as definições globais de funcionamento do analisador léxico para a linguagem.

- **Alfabeto de Entrada:** O scanner reconhece os caracteres da tabela ASCII imprimíveis (códigos `32` a `126`), além dos caracteres de controle de formatação: tabulação (`\t`), retorno de carro (`\r`) e quebra de linha (`\n`). Caracteres fora deste escopo geram erro léxico.
    
- **Sensibilidade a Maiúsculas e Minúsculas (Case-sensitive):** A linguagem é estritamente _case-sensitive_. Identificadores e palavras reservadas seguem esta regra estritamente (ex.: `while` é uma palavra reservada reconhecida, mas `While` ou `WHILE` seriam tratados como identificadores comuns).
    
- **Espaços em Branco e Comentários:**
    
    - **Espaços em branco:** Os caracteres de espaço ( ), tabulação (`\t`), `\r` e `\n` atuam exclusivamente como delimitadores para separar os tokens e são descartados pelo scanner, não gerando nenhum token final.
        
    - **Comentários de linha:** Iniciados pelos caracteres `//`, o scanner ignora todos os caracteres subsequentes até encontrar uma quebra de linha (`\n`) ou o fim do arquivo (EOF).
        
    - **Comentários de bloco:** Para manter a simplicidade do autômato finito exigido nesta etapa, a linguagem não suporta comentários de bloco; consequentemente, não há aninhamento de comentários.
        
- **Regra de Desambiguação (Maximal Munch):** O analisador léxico sempre tenta consumir o prefixo válido mais longo possível do código-fonte antes de classificar e emitir um token. Por exemplo, ao ler a sequência `<=`, o scanner emitirá o token único do operador relacional menor ou igual (`<=`), em vez de emitir dois tokens soltos e consecutivos (`<` e `=`).
    

## 2. Categorias de Tokens

O léxico da linguagem é composto pelas cinco categorias principais exigidas, detalhadas na tabela a seguir:

|**Token**|**Notação Formal (Regex/EBNF)**|**Exemplos Válidos**|**Decisões de Projeto e Limites**|
|---|---|---|---|
|**Identificador**|`[a-zA-Z][a-zA-Z0-9]*`|`total`, `x1`, `calculaValor`|Deve obrigatoriamente iniciar com uma letra. O uso de _underscore_ (`_`) não é permitido para simplificar as transições do autômato. Não há tamanho máximo pré-definido.|
|**Palavra Reservada**|Casamento exato na tabela|`if`, `while`, `int`, `return`|Segue exatamente o mesmo padrão estrutural do identificador, mas o lexema é buscado em uma tabela fechada em memória antes da emissão do token.|
|**Literal Numérico**|`[0-9]+ (\.[0-9]+)?`|`10`, `3.14`, `0`|Suporta números inteiros e números decimais (ponto flutuante) básicos. Não suporta notação científica nem bases hexadecimais/binárias, atuando apenas com base 10.|
|**String**|`" [^"\n\r]* "`|`"texto"`, `"ola mundo"`|Delimitada estritamente por aspas duplas (`"`). Não permite quebra de linha interna (strings multilinha não suportadas) e não possui caracteres de escape (como `\"` para escapar aspas). Fim de arquivo (EOF) ou fim de linha antes do fechamento das aspas duplas gera um erro léxico tratado.|
|**Operador**|`== \| != \| <= \| >= \| < \| > \| = \| + \| - \| * \| /`|`=`, `==`, `<=`, `+`|Contempla operadores matemáticos e relacionais básicos, com formas simples (1 caractere) e compostas (2 caracteres) resolvidas corretamente pela regra do _maximal munch_.|

_(Nota: Símbolos de pontuação estrutural como `(`, `)`, `{`, `}`, `,` e `;` também compõem os delimitadores da linguagem)._

## 3. Lista Fechada de Palavras Reservadas

A linguagem possui uma lista fechada de palavras-chave que cobre todo o escopo de tipos básicos, estruturas de controle e procedimentos exigidos:

Tipos Básicos:

- `int` (inteiro)
    
- `double` (ponto flutuante)
    
- `bool` (booleano)
    
- `char` (caractere)
    
- `string` (cadeia de caracteres)
    
- `void` (tipo de retorno nulo utilizado em procedimentos)
    

Estruturas de Controle (Seleção e Repetição):

- `if`
    
- `else`
    
- `while`
    

Controle de Funções e Valores Literais:

- `return` (retorno de funções)
    
- `true` (valor lógico verdadeiro)
    
- `false` (valor lógico falso)