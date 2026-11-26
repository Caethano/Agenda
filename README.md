# Agenda – Gerenciamento de Contatos com SQLite (AD3)

**Disciplina:** STC0328 – Programação para Dispositivos Móveis (IFC Camboriú, 2026/2)
**Aluno:** Caethano Rampelotti da Rosa
**Atividade:** AD3 – Implementação (Unidade 3: Comunicação entre processos e Persistência de dados)

Aplicativo Android nativo em **Java** que implementa um CRUD completo de contatos (nome, e-mail, telefone e foto), com dados persistidos em **SQLite** e organização em **MVC**. Também usa **SharedPreferences** e **Intents**, como apresentado na unidade.

## Funcionalidades

| Ação | Como fazer | Operação |
|---|---|---|
| Listar contatos | Tela inicial (ordem alfabética) | READ |
| Adicionar contato | Botão flutuante **+** | CREATE |
| Alterar contato | Toque no contato | UPDATE |
| Excluir contato | Toque longo e confirmação | DELETE |
| Foto do contato | Botão "Selecionar foto" no formulário | URI salva no BD |
| Nome do dono da agenda | Menu ⋮ › "Meu nome" | SharedPreferences |

## Arquitetura (MVC)

```
com.caethano.agenda
├── model/                       ← Camada de Dados
│   ├── Contato.java             Entidade (Bean, Serializable)
│   ├── ContatoDAO.java          Operações do CRUD no banco
│   └── DatabaseHelper.java      Criação e migrations do SQLite (SQLiteOpenHelper)
├── controller/                  ← Camada de Negócio
│   └── ContatoController.java   Regras (validação) e ponte View ↔ Model
├── MainActivity.java            ← View: lista (ListView + ArrayAdapter)
├── AdicionarContatoActivity.java← View: formulário de inclusão/alteração
└── FotoUtil.java                Exibição da foto a partir da URI
```

Fluxo: **Activity → Controller → DAO → DatabaseHelper → SQLite**, e o resultado volta convertido em objetos `Contato`.

## Mecanismos da Unidade 3 utilizados

- **Intent explícita:** abre o formulário e envia o contato selecionado (`putExtra("contato", contato)`).
- **Intent implícita / Content Provider:** o seletor de fotos (`ActivityResultContracts.OpenDocument`) pede a imagem a outro app (galeria ou arquivos) e recebe uma URI com permissão persistente.
- **SharedPreferences:** guarda o nome do dono da agenda (arquivo privado, `MODE_PRIVATE`).
- **SQLite:** banco `contatos.db`, tabela `contato`, consultas parametrizadas (`id=?`).

## Melhorias em relação ao código-base do material

- A validação de nome e telefone agora **interrompe** o salvamento (`return`). Antes, o contato era salvo mesmo com o aviso.
- Foi incluído o construtor `Contato(id, nome, email, telefone, foto)` usado na alteração, e a ordem dos parâmetros ficou consistente entre Controller e Model.
- O `onUpgrade` faz a migração 1 → 2 **sem apagar** os contatos existentes.
- O formulário trata inclusão e alteração, carrega e salva a foto e mostra uma mensagem quando a lista está vazia.

## Como executar

1. Abra a pasta no **Android Studio** (File › Open) e aguarde o Gradle sincronizar.
2. Conecte o celular (depuração USB, pode usar o **scrcpy**) ou use um emulador.
3. Clique em **Run ▶**.

Requisitos: Android 7.0 (API 24) ou superior.
