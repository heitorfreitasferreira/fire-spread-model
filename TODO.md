# TODOS

## Correções Yuri

- [ ] Adequar construtor para Reticulado criado a partir de JSON
- [ ] Corrigir saída quando simulação é executada a partir de JSON
  - Deve sair todas as estapas da simulação em um array de matriz
  - O arquivo de saída deve ser o especificado no argumento de linha de comando
  - Não gerar arquivos adicionais além do especificado

### Testes
Para testar, gere um arquivo JAR na raíz do projeto e execute o seguinte comando para utilizar o arquivo de input na pasta `tests`:

```bash
java -jar <arquivo-jar> -f ./tests/input.json ./tests/test.json
```
O arquivo de entrada deve ser `./tests/input.json` (primeiro argumento para parametro -f).
O arquivo de saída deve sair em `./tests/test.json` (segundo argumento para parametro -f).

## Otimizações

- [x] Temporizador pra contar o tempo de execução
- [ ] Alterar a granularidade de escrita para salvar de 10 em 10
- [ ] Acertar a ordem de execução do jeito que está salvando
  - [x] Modulo de visualização para facilitar o entendimento da evolução do reticulado
- [ ] Paralelizar as execuções em threads multiplas de 8
  - [ ] Ir alocan``do as threads de acordo com o final das simulações
- [ ] Usar os dados do reticulado pra setar a seed dos numeros aleatorios
- [ ] Simplificar construtor de reticulado
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

## Artigos e leituras

- [ ] Daniele
- [ ] ENIAC
