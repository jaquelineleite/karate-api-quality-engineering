# Revisão rápida — análise de sistemas, UML e OO

**UML não é Figma:** UML é uma linguagem padronizada de modelagem. Miro e diagrams.net são ferramentas onde podemos desenhá-la. Figma é predominantemente design de interface.

**Três perguntas comuns:**
- **Qual a diferença entre caso de uso e sequência?** Caso de uso descreve objetivos e atores; sequência descreve mensagens trocadas ao longo do tempo.
- **Qual a diferença entre classe e objeto?** Classe é o tipo/molde; objeto é uma instância com valores concretos.
- **Qual a diferença entre encapsulamento e abstração?** Encapsulamento controla acesso ao estado; abstração destaca o comportamento essencial e oculta detalhes irrelevantes.

**Como analisar um requisito:** identificar ator, objetivo, regras de negócio, entradas, saídas, exceções, dependências e critérios de aceite.

**Exemplo do laboratório:** “Quando o serviço de pagamento estiver indisponível, a reserva deve permanecer persistida com estado controlado PAYMENT_FAILED.” Testar cenário feliz **e** falha; registrar evidências; verificar consistência e rastreabilidade por X-Correlation-ID.

**SQL e modelagem de dados:** distinguir entidade, atributo, chave primária, chave estrangeira e cardinalidade. Não afirmar Oracle: este laboratório usa PostgreSQL.

**Pergunta de entrevista:** “Você já trabalhou com UML?” Resposta transparente: “Tenho base de orientação a objetos e análise de requisitos. Estruturei um estudo aplicado com diagramas UML de sequência e classes para documentar um laboratório de integração Booking/Payment. Estou consolidando o uso de UML em análise e projeto de sistemas.”

**Próximo passo opcional:** conferir código e banco reais e transformar o modelo conceitual em modelo de implementação, sem adicionar diagramas por adicionar.
