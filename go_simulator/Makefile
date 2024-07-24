# Variáveis
GOCMD = go
GOBUILD = $(GOCMD) build
GOCLEAN = $(GOCMD) clean
GOTEST = $(GOCMD) test
BINARY_NAME = fire-spread-simulator

all: build

build:
	$(GOBUILD) -o ./bin/$(BINARY_NAME) ./cmd/main.go

run: build
	./bin/$(BINARY_NAME) --config ./input.json

river: build
	@times=$(TIMES); \
	timestamp=$$(date +%Y-%m-%d_%H-%M-%S); \
	dir=./../model_evolution/river_test/$$timestamp; \
	mkdir -p $$dir; \
	parallel --keep-order --tag "./bin/$(BINARY_NAME) --seed {} >> $$dir/{}.txt" ::: $$(seq 0 $$((times - 1)))

test:
	$(GOTEST) ./... -v

clean:
	$(GOCLEAN)
	rm -f $(BINARY_NAME)