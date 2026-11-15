# 🎯 Super Forca

Jogo da forca para **Android**, feito em **Kotlin** com **Jetpack Compose**. O jogador escolhe um tema, tenta descobrir 5 palavras contra o relógio, salva a pontuação com suas iniciais e disputa um ranking local. As palavras ficam na nuvem (**Firebase Firestore**) e podem ser gerenciadas pela área de **administração** do próprio app, que também permite editar o ranking.

---

## 📑 Sumário

- [O que é](#-o-que-é)
- [Funcionalidades](#-funcionalidades)
- [Como jogar](#-como-jogar)
- [Regras do jogo](#-regras-do-jogo)
- [Tecnologias](#-tecnologias)
- [Prints das telas](#-prints-das-telas)
- [Como rodar o projeto](#-como-rodar-o-projeto)
- [Como o projeto funciona](#-como-o-projeto-funciona)
- [Estrutura de pastas](#-estrutura-de-pastas)
- [Configuração do Firebase](#-configuração-do-firebase)
- [Personalizando o jogo](#-personalizando-o-jogo)
- [Limitações conhecidas](#-limitações-conhecidas)

---

## 📱 O que é

O **Super Forca** é uma versão moderna do clássico jogo da forca. Em vez de uma única palavra, cada partida tem **5 palavras** de um tema escolhido e um **cronômetro** correndo. Quanto mais palavras o jogador acertar antes do tempo ou das tentativas acabarem, maior a pontuação.

Ao final, o jogador registra **até 4 letras** (estilo fliperama) e sua pontuação entra no **ranking** e no **histórico** de partidas, armazenados no próprio aparelho.

## ✨ Funcionalidades

- 🎮 **Jogo** com 3 temas: *Alimentos*, *Animais* e *Objetos*
- ☁️ **Palavras na nuvem**: o jogo sorteia as palavras direto do Firestore, as mesmas que a tela Admin edita
- 🔄 **Indicador de carregamento** e mensagens de erro ao buscar as palavras
- ⏱️ **Cronômetro** de 120 segundos por partida
- 🧍 **Boneco da forca** que vai sendo desenhado a cada erro (6 partes)
- ⌨️ **Teclado virtual** na tela, com letras certas e erradas destacadas
- 🏆 **Ranking** ordenado por pontuação, com destaque para o 1º lugar
- 📜 **Histórico** de partidas por data, com busca por nome
- 🛠️ **Admin de palavras**: adicionar, editar e excluir palavras de cada tema (as mudanças valem para as próximas partidas)
- 🥇 **Admin de ranking**: editar pontuações e remover partidas
- ❓ **Tutorial** "Como jogar" na tela inicial

## 🕹️ Como jogar

1. Na tela inicial, toque em **Jogar**.
2. Escolha um **tema** (o app busca as palavras na internet; aguarde o indicador de carregamento).
3. Toque nas letras do teclado para tentar descobrir a palavra.
4. Ao acertar uma palavra, ganha pontos e passa para a próxima (as tentativas voltam ao máximo).
5. A partida termina quando:
   - o **tempo** acaba,
   - as **tentativas** acabam (boneco completo), ou
   - todas as **5 palavras** forem acertadas.
6. Digite suas **iniciais (até 4 letras)** e toque em **Salvar**. Você será levado ao ranking.

## 📏 Regras do jogo

| Regra | Valor |
|---|---|
| Palavras por partida | 5 |
| Tentativas (erros permitidos) por palavra | 6 |
| Pontos por palavra acertada | 100 |
| Tempo por partida | 120 segundos |
| Iniciais do jogador | até 4 letras (A–Z) |

Esses valores ficam em `JogoViewModel.kt` e podem ser alterados facilmente (veja [Personalizando o jogo](#-personalizando-o-jogo)).

## 🧰 Tecnologias

| Área | Tecnologia |
|---|---|
| Linguagem | Kotlin 2.2 |
| Interface | Jetpack Compose + Material 3 |
| Navegação | Navigation Compose |
| Arquitetura | MVVM (ViewModel + StateFlow) + Repository |
| Banco local | Room (SQLite) com KSP |
| Banco remoto | Firebase Firestore |
| Assincronismo | Kotlin Coroutines / Flow |
| Build | Gradle (Kotlin DSL) + Version Catalog |

**Versões do Android:** `minSdk 24` (Android 7.0) · `targetSdk / compileSdk 37`

## 🖼️ Prints das telas

As capturas de tela do app estão no arquivo [`Prints Jogo da Forca.pdf`](Prints%20Jogo%20da%20Forca.pdf), na raiz do repositório.

## 🚀 Como rodar o projeto

### Pré-requisitos

- **[Android Studio](https://developer.android.com/studio)** em versão recente (o projeto usa o Android Gradle Plugin 9.x, então versões antigas do Android Studio não abrem o projeto)
- **JDK** compatível com o Gradle do projeto (o Android Studio já inclui um; o projeto está configurado para o toolchain JDK 25 — o Gradle tenta baixá-lo automaticamente)
- **Android SDK 37** instalado (pelo SDK Manager do Android Studio)
- Um **emulador** (AVD) ou um **celular Android** com depuração USB ativada
- **Conexão com a internet**: para baixar as dependências do Gradle e também para **jogar**, já que as palavras de cada partida são buscadas no Firestore

### Passo a passo

1. **Clone o repositório**

   ```bash
   git clone https://github.com/0Isabella/Super_Forca.git
   cd Super_Forca
   ```

2. **Abra no Android Studio**
   `File > Open...` e selecione a pasta `Super_Forca`.

3. **Aguarde o Gradle Sync** terminar (barra de progresso no canto inferior). Na primeira vez pode demorar alguns minutos.

4. **Escolha onde rodar**
   - *Emulador:* `Tools > Device Manager > Create Device` (qualquer celular com Android 7.0 ou superior).
   - *Celular físico:* ative as **Opções do desenvolvedor** e a **Depuração USB**, conecte o cabo e autorize o computador.

5. **Execute** clicando em ▶️ **Run 'app'** (ou `Shift + F10`).

### Rodando pela linha de comando (opcional)

Com um dispositivo/emulador conectado:

```bash
# Linux / macOS
./gradlew installDebug

# Windows
gradlew.bat installDebug
```

Para apenas gerar o APK de debug:

```bash
./gradlew assembleDebug
# saída: app/build/outputs/apk/debug/app-debug.apk
```

### Rodando os testes

```bash
./gradlew test                    # testes unitários
./gradlew connectedAndroidTest    # testes instrumentados (precisa de dispositivo)
```

## ⚙️ Como o projeto funciona

O app segue o padrão **MVVM** em camadas. A interface (Compose) apenas observa o estado exposto pelos ViewModels, e os dados são acessados através de Repositories.

```
 UI (Compose)  ──►  ViewModel  ──►  Repository  ──►  Fonte de dados
 ui/screens         viewmodel       data/repository    data/local (Room)
                                                       data/remote (Firestore)
```

### Navegação

Definida em `navigation/AppNavigation.kt`, com uma barra inferior (**Início · Histórico · Admin**):

| Rota | Tela | Função |
|---|---|---|
| `inicio` | `TelaInicio` | Logo animada, botão Jogar e tutorial |
| `jogo` | `TelaJogo` | Escolha de tema, partida e salvamento da pontuação |
| `historico` | `TelaHistorico` | Ranking e histórico, com busca por nome |
| `admin` | `TelaAdmin` | CRUD de palavras por tema |
| `admin_ranking` | `TelaAdminRanking` | Editar/remover partidas do ranking |

### Fluxo de uma partida (`JogoViewModel`)

1. O jogador escolhe um tema → `iniciarJogo(tema)` mostra o estado de *carregando*, busca as palavras do tema no Firestore, sorteia 5 (embaralhadas) e só então inicia o cronômetro. Se a busca falhar, ou o tema não tiver palavras cadastradas, a tela de temas exibe uma mensagem de erro.
2. A cada letra tocada, `tentarLetra(letra)`:
   - **Acerto:** a letra é revelada; se a palavra for completada, soma 100 pontos, mostra a palavra por ~0,8 s e avança para a próxima.
   - **Erro:** perde uma tentativa e uma parte do boneco aparece; com 0 tentativas o jogo termina.
3. O jogo vai para a fase `FIM` com um motivo (`TEMPO_ESGOTADO`, `SEM_TENTATIVAS` ou `TODAS_ACERTADAS`).
4. O jogador informa as iniciais e a partida é gravada no Room pelo `RankingViewModel`.

O estado da partida é um único objeto imutável (`JogoUiState`) exposto por `StateFlow`; as três fases são `ESCOLHENDO_TEMA → JOGANDO → FIM`.

### Armazenamento de dados

**Local (Room) — ranking e histórico.** Tabela `partidas` (`forca.db`):

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Int | Chave primária (auto) |
| `nomeJogador` | String | Iniciais (até 4 letras) |
| `pontuacao` | Int | Pontos da partida |
| `tema` | String | Tema jogado |
| `data` | Long | Timestamp da partida |

O ranking é a mesma tabela ordenada por `pontuacao DESC`; o histórico, por `data DESC`.

**Remoto (Firestore) — palavras do jogo e da administração.** Uma coleção por tema, cada documento com o campo `texto`. O jogo faz uma busca única ao iniciar a partida (`buscar`), e a tela Admin acompanha a lista em tempo real (`observar`):

| Tema | Coleção |
|---|---|
| Alimentos | `palavras_alimentos` |
| Animais | `palavras_animais` |
| Objetos | `palavras_objetos` |

Como jogo e Admin usam as mesmas coleções, **uma palavra adicionada, editada ou excluída na Admin passa a valer na próxima partida**. As palavras passam por validação e normalização: convertidas para MAIÚSCULAS, sem acentos (`FEIJÃO → FEIJAO`), sem espaços, apenas letras, **máximo 10 letras** e sem duplicatas no mesmo tema.

## 🗂️ Estrutura de pastas

```
app/src/main/java/com/example/forca/
├── MainActivity.kt              # Ponto de entrada + barra de navegação inferior
├── navigation/                  # Rotas e NavHost
│   ├── AppNavigation.kt
│   └── Rota.kt
├── ui/
│   ├── screens/                 # Telas Compose (Início, Jogo, Histórico, Admin...)
│   └── theme/                   # Cores, tipografia e tema
├── viewmodel/                   # JogoViewModel, RankingViewModel, AdminViewModel...
└── data/
    ├── model/                   # Tema, Palavra
    ├── local/                   # Room: AppDatabase, Partida, PartidaDao
    ├── remote/                  # PalavraFirestoreDataSource (acesso ao Firestore)
    └── repository/              # Repositories usados pelos ViewModels
```

## 🔥 Configuração do Firebase

O repositório já inclui o arquivo `app/google-services.json` (projeto `superforca-000`, pacote `com.example.forca`), então **o app compila e roda sem configuração adicional**.

Se quiser usar o **seu próprio** projeto Firebase (por exemplo, ao fazer um fork):

1. Crie um projeto em [console.firebase.google.com](https://console.firebase.google.com).
2. Adicione um app **Android** com o pacote `com.example.forca`.
3. Baixe o `google-services.json` e substitua o arquivo em `app/google-services.json`.
4. No console, abra **Firestore Database** e crie o banco.
5. Ajuste as **regras de segurança** para permitir leitura/escrita nas coleções `palavras_*` (durante o desenvolvimento, o modo de teste serve; em produção, restrinja o acesso).
6. **Cadastre as palavras.** O app não traz palavras embutidas: crie-as pela tela **Admin** do app ou direto no console, nas coleções `palavras_alimentos`, `palavras_animais` e `palavras_objetos`, com documentos no formato `{ "texto": "PIZZA" }`. Recomenda-se **pelo menos 5 palavras por tema**; com menos, a partida terá menos rodadas.

> ⚠️ O **jogo depende do Firestore**. Sem internet, com regras que bloqueiem o acesso ou com um tema sem palavras, a partida não inicia e a tela de temas mostra uma mensagem de erro (por exemplo, *"Nenhuma palavra cadastrada em Animais."*).

## 🎛️ Personalizando o jogo

| O que alterar | Onde |
|---|---|
| Tentativas, pontos, tempo e nº de palavras | constantes no topo de `viewmodel/JogoViewModel.kt` |
| Palavras usadas no jogo | tela **Admin** do app ou coleções `palavras_*` no console do Firestore |
| Adicionar um novo tema | novo item no enum `Tema` (`data/model/Tema.kt`), com título e nome da coleção; ele passa a aparecer no jogo e na Admin |
| Limite de letras de uma palavra | `MAX_LETRAS_PALAVRA` em `data/model/Palavra.kt` |
| Cores | `ui/theme/Color.kt` |

## ⚠️ Limitações conhecidas

- **O jogo exige internet** (e um Firestore com palavras cadastradas), pois não há lista de palavras offline.
- A área **Admin não tem autenticação**: qualquer pessoa com o app pode acessá-la.
- O **ranking é local**: cada aparelho tem o seu, não há ranking online.
- O banco Room usa `fallbackToDestructiveMigration`: ao mudar a versão do esquema, os dados salvos são apagados.
- `TelaTutorial` ainda é uma versão simples (um diálogo com texto curto).

Feito com 💚 em Kotlin e Jetpack Compose.
