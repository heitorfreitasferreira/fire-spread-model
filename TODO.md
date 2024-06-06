# TODOS

## Otimizações

- [x] Temporizador pra contar o tempo de execução
- [ ] Alterar a granularidade de escrita para salvar de 10 em 10
- [ ] Acertar a ordem de execução do jeito que está salvando
  - [x] Modulo de visualização para facilitar o entendimento da evolução do reticulado
- [ ] Paralelizar as execuções em threads multiplas de 8
  - [ ] Ir alocan``do as threads de acordo com o final das simulações
- [ ] Usar os dados do reticulado pra setar a seed dos numeros aleatorios
- [x] Simplificar construtor de reticulado
- [ ] [ndvi](https://www.myfarm.com.br/ndvi/)

## Melhorias no modelo

- [x] Matriz retangular não quadrada
- [x] Rio de fogo
  - [x] Versão com matriz
  - [x] Versão com vizinhança maior com  probabilidade menor
- [ ] Propagação pelo ar
  - [ ] Versão com vizinhança maior com  probabilidade menor
  - [ ] Não criar um novo reticulado
    - Usar o mesmo reticulado, ir diminuindo a prob de acordo com a distância da central

## Algoritmo Genético

- [ ] Identificar por que o algoritmo genético está convergindo tão rapido e na inicialização randomica já nasce um valor muito bom

## Artigos e leituras

- [ ] Daniele
- [ ] ENIAC

## Ideias de experimento

### Faisca

colocar rio no meio do mapa, colocar fogo no meio da metade, com vento apontando pra outra metade, rodar X passos de tempo, contar em qual iteração o fogo atravessou o rio, e quanto queimou no lado q n começou com fogo