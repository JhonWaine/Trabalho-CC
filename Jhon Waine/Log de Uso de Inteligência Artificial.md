# Log de Uso de Inteligência Artificial

**Disciplina:** Construção de Compiladores  
**Instituição:** Universidade Federal de Uberlândia (UFU)  
**Aluno:** Jhon Waine Mendes Gonçalves  
**Matrícula:** 12021BCC028  
**Checkpoint:** 1 — Especificação Léxica e Analisador Léxico  

---

Conforme exigido nas diretrizes do projeto, este documento registra o uso de ferramentas de Inteligência Artificial (Gemini) durante o desenvolvimento deste entregável. A IA foi utilizada de forma restrita às permissões do roteiro, atuando exclusivamente como assistente para dúvidas de sintaxe, formatação e geração de código repetitivo (*boilerplate*), operando sob as seguintes frentes:

### 1. Especificação Léxica
* **Uso:** Revisão e formatação estrutural do documento.
* **Detalhes:** A lógica, os limites da linguagem e a definição dos tokens foram elaborados manualmente. A IA foi acionada apenas para formatar o arquivo final em Markdown (`especificacao-lexica.md`), montando a estrutura da tabela de tokens e alinhando as notações (Regex/EBNF) para garantir uma apresentação limpa e legível.

### 2. Implementação do Scanner (Java)
* **Uso:** Geração de *boilerplate* de leitura de stream e revisão de sintaxe Java.
* **Detalhes:** O uso da IA neste componente limitou-se à criação do código de infraestrutura para percorrer a *string* do código-fonte (os métodos utilitários `peek`, `advance` e `hasNext`, além do controle de linha/coluna). O mapeamento dos estados do AFD para o código Java e a implementação da regra de *maximal munch* foram codificados de forma manual. A IA também foi consultada pontualmente para tirar dúvidas de sintaxe sobre como evitar que o programa parasse abruptamente com *exceptions* durante o tratamento de erros léxicos.

### 3. Desenho dos Autômatos Finitos (AFDs)
* **Uso:** Geração de código visual a partir de sintaxe específica (LaTeX/TikZ).
* **Detalhes:** A modelagem dos autômatos foi feita logicamente no papel. Para a digitalização, a IA foi utilizada para traduzir a lógica de estados e transições (já definida previamente) para o código em LaTeX, utilizando a biblioteca TikZ, a fim de gerar os diagramas em PDF presentes na pasta `automatos/`.

### 4. Suíte de Testes
* **Uso:** Criação de *boilerplate* para os asserts.
* **Detalhes:** A IA foi utilizada para gerar a estrutura repetitiva dos métodos de verificação (assertivas simples na classe `ScannerTest`), poupando tempo de digitação. Os casos de uso e as categorias testadas foram guiados pelas restrições da especificação.

---
*Declaro que o analisador léxico não foi gerado inteiramente por inteligência artificial ("prompt de geração total"). O código entregue foi estruturado, revisado e compreendido por mim, e estou apto a justificar a implementação, estado por estado do autômato, durante a eventual arguição oral.*