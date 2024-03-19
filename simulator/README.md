# Modelo de Propagação de Incêndios para o Cerrado Brasileiro

Este projeto em Java, "Modelo de Propagação de Incêndios", implementa um modelo estocástico aprimorado utilizando autômatos celulares para simular incêndios florestais especificamente no bioma do Cerrado brasileiro. Aproveitando o JCommander para a configuração de parâmetros via linha de comando, ele oferece uma ferramenta flexível e poderosa para pesquisadores e ambientalistas modelarem e entenderem a dinâmica dos incêndios florestais. Esta simulação visa auxiliar no desenvolvimento de estratégias de combate a incêndios mais eficazes e no planejamento de esforços de mitigação, representando com precisão as complexidades da propagação do fogo, influenciadas por fatores como tipos de vegetação, correntes de vento e umidade do ar.

## Funcionalidades

- Simulação detalhada de incêndios florestais baseada em autômatos celulares e regras estocásticas.
- Personalizável através de argumentos de linha de comando para configurações de simulação dinâmicas.
- Incorpora fatores ambientais como vegetação, vento e umidade.

## Requisitos

- Java JDK 21 ou superior.
- Maven para construir e gerenciar o projeto.

## Construindo o Projeto

1. Clone o repositório para sua máquina local.
2. Navegue até o diretório do projeto.
3. Construa o projeto usando o Maven:
    ```bash
    mvn package
    ```

Isso compilará o código-fonte e o empacotará em um arquivo JAR executável localizado no diretório `target/`, nomeado `fire-spread-model-1.0-SNAPSHOT.jar`.

## Executando a Simulação

Após construir o projeto, execute a simulação usando o seguinte comando:

```bash
java -jar target/fire-spread-model-1.0-SNAPSHOT.jar [opções]
```

### Exemplos de Uso

#### Buildar o projeto

Primeiro, se você ainda não o fez, construa o projeto usando o Maven:

```bash
mvn package
```

#### Executando uma única simulação

```bash
java -jar target/fire-spread-model-1.0-SNAPSHOT.jar --mode "single-simulation" --input "input.json" --output "results.json"
```

#### Executando o Módulo de Algoritmo Genético

```bash
java -jar target/fire-spread-model-1.0-SNAPSHOT.jar --mode "genetic-algorithm"
```

#### Executando o Módulo de Algoritmo Genético com Parâmetros Personalizados

```bash
java -jar target/fire-spread-model-1.0-SNAPSHOT.jar --mode "genetic-algorithm" --number-of-generations 50 --population-size 200 --mutation-rate 0.05 --mutation-prob 0.2 --crossover-rate 0.8 --elitism-rate 0.15 --tournament-size 3 --max-iterations 150 --crossover-blx-alpha 0.005
```

### Opções de Linha de Comando

A simulação suporta uma variedade de opções de linha de comando permitindo uma personalização detalhada do processo de simulação. Abaixo estão as opções disponíveis juntamente com suas descrições:

- `--mode`: Especifica o modo de operação da simulação. Isso determina o comportamento principal do programa ao ser executado. Existem duas opções válidas para este parâmetro:
  - `single-simulation`: Executa uma única instância da simulação com os parâmetros especificados. Este modo é adequado para conduzir um cenário de simulação específico, analisando seus resultados com base na configuração fornecida. Exemplo: `--mode single-simulation`.
  - `genetic-algorithm`: Invoca o modo de algoritmo genético, que utiliza técnicas de algoritmos genéticos para otimizar ou explorar parâmetros de simulação. Este modo é destinado a cenários em que deseja-se melhorar iterativamente os parâmetros de simulação para alcançar resultados desejados ou explorar o espaço de parâmetros para configurações ótimas. Exemplo: `--mode genetic-algorithm`.
  - Qualquer outro valor fornecido a `--mode` resultará em uma `IllegalArgumentException` indicando uma seleção de modo inválida.

- `--input`: O caminho para um arquivo de entrada JSON que contém a configuração da simulação. Este arquivo deve definir as condições iniciais e os parâmetros para a simulação, como o layout da área simulada, locais iniciais de incêndio e condições ambientais. A estrutura do arquivo JSON deve corresponder ao formato de entrada esperado pelo modelo de simulação. Exemplo: `--input config.json`.

- `--output`: O caminho do arquivo onde os resultados da simulação serão salvos. Se não especificado, o caminho padrão `./output.json` é usado. O arquivo de saída inclui resultados detalhados da simulação, como a progressão do fogo, áreas afetadas e o estado final do ambiente de simulação. Exemplo: `--output results.json`.

- `--number-of-generations`: Define o número de gerações (etapas de simulação) a serem executadas. Cada geração representa um passo no tempo do processo de simulação. Aumentar o número de gerações permite tempos de simulação mais long

os. O valor padrão é 20. Exemplo: `--number-of-generations 50`.

- `--population-size`: Define o tamanho da população na simulação. No contexto de simulações de incêndios florestais, isso pode representar o número de partículas de fogo distintas ou unidades sendo simuladas. O valor padrão é 100. Exemplo: `--population-size 200`.

- `--mutation-rate`: Determina a taxa de mutação dentro da simulação. Isso pode afetar vários fatores, como a taxa de propagação ou comportamento do fogo sob diferentes condições. O valor padrão é 0.1. Exemplo: `--mutation-rate 0.05`.

- `--mutation-prob`: Especifica a probabilidade de ocorrência de mutação em cada geração para qualquer unidade da população. Isso adiciona um elemento de aleatoriedade ou variabilidade à simulação, refletindo a natureza imprevisível dos incêndios florestais reais. O valor padrão é 0.1. Exemplo: `--mutation-prob 0.2`.

- `--crossover-rate`: Define a taxa na qual ocorre o cruzamento entre unidades na população. Na simulação de incêndios florestais, isso pode simular a fusão ou interação de frentes de fogo separadas. O valor padrão é 0.9. Exemplo: `--crossover-rate 0.8`.

- `--elitism-rate`: A proporção das unidades de melhor desempenho na população que são preservadas inalteradas para a próxima geração. Isso garante que estratégias ou comportamentos bem-sucedidos sejam mantidos. O valor padrão é 0.1. Exemplo: `--elitism-rate 0.15`.

- `--tournament-size`: Nos processos de seleção, define o tamanho do torneio para escolher quais unidades se reproduzem. Esse parâmetro impacta a pressão de seleção. O valor padrão é 2. Exemplo: `--tournament-size 3`.

- `--max-iterations`: O número máximo de iterações que a simulação deve executar antes de parar automaticamente. Serve como um dispositivo de segurança para evitar corridas de simulação excessivamente longas ou infinitas. O valor padrão é 100. Exemplo: `--max-iterations 150`.

- `--crossover-blx-alpha`: Especifica o parâmetro BLX-alpha usado na operação de cruzamento, impactando como os descendentes são gerados a partir dos pais. Esse parâmetro pode influenciar a diversidade da população. O valor padrão é 0.001. Exemplo: `--crossover-blx-alpha 0.005`.

Cada opção permite o ajuste fino da simulação para refletir diferentes cenários, condições e comportamentos observados em incêndios florestais, fornecendo uma ferramenta poderosa para pesquisa e análise.

## Contribuindo

Contribuições para o projeto Modelo de Propagação de Incêndios são bem-vindas. Entre em contato comigo em @heitorfreitasferreira para quaisquer perguntas, sugestões ou melhorias.