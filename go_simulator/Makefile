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

test:
	$(GOTEST) ./...

clean:
	$(GOCLEAN)
	rm -f $(BINARY_NAME)