# Referência técnica — UML e orientação a objetos

Material de consulta complementar à documentação de domínio em [Modelagem UML](./README.md).

## Notações e finalidades

| Notação | Aplicação |
|---|---|
| Diagrama de sequência (UML) | Ordem temporal das mensagens entre participantes |
| Diagrama de classes (UML) | Estrutura estática, atributos, operações e relacionamentos |
| Diagrama de casos de uso (UML) | Atores, objetivos e fronteira do sistema |
| BPMN | Modelagem de processos de negócio |
| Modelo entidade-relacionamento | Estrutura lógica de dados e cardinalidades |

**UML** é uma linguagem de modelagem, não uma ferramenta. Miro e diagrams.net podem ser usados para desenhar diagramas; ferramentas CASE especializadas apoiam atividades de engenharia de software.

## Orientação a objetos — conceitos de referência

| Conceito | Definição técnica |
|---|---|
| Classe | Definição de atributos e operações de um tipo |
| Objeto | Instância de uma classe |
| Encapsulamento | Controle de acesso ao estado e aos comportamentos |
| Abstração | Representação das características relevantes do domínio |
| Herança | Especialização de um tipo a partir de outro |
| Polimorfismo | Diferentes implementações de um mesmo contrato |
| Associação | Relação estrutural entre elementos |
| Composição | Relação todo-parte com dependência de ciclo de vida |

### Aplicação ao domínio

No modelo conceitual, `Reserva` é uma entidade de negócio com estado e operações. `confirmar()` é uma operação de domínio **ilustrativa**, não necessariamente um método implementado no serviço. O diagrama de sequência, por sua vez, descreve mensagens **entre serviços**, não relações de herança entre classes.

### Critérios para evolução do modelo

- Conferir os contratos HTTP e as entidades efetivamente implementadas.
- Distinguir classes de domínio, serviços, DTOs e tabelas.
- Verificar cardinalidades e transições de estado contra regras de negócio.
- Relacionar diagramas a cenários de teste apenas quando houver evidência.
- Evitar introduzir abstrações que não representem uma necessidade real do sistema.

Este arquivo é referência conceitual; a documentação principal do repositório permanece orientada ao produto, arquitetura, requisitos e qualidade.
