include .env
# Variáveis

BINARY_NAME = fire-spread-simulator

all: build

build: migrate-up
	go build -o ./bin/$(BINARY_NAME) ./main.go

run: build
	./bin/$(BINARY_NAME) --config ./input.json

river: build
	@times=$(TIMES); \
	timestamp=$$(date +%Y-%m-%d_%H-%M-%S); \
	dir=./../model_evolution/river_test/$$timestamp; \
	mkdir -p $$dir; \
	parallel --keep-order --tag "./bin/$(BINARY_NAME) --seed {} >> $$dir/{}.txt" ::: $$(seq 0 $$((times - 1)))

test:
	go test ./... -v

clean:
	go clean
	rm -f $(BINARY_NAME)


# MIGRATIONS

migrate-up:
	migrate -path $(MIGRATION_DIR) -database "sqlite3://$(DB_URL)" -verbose up
migrate-down:
	migrate -path $(MIGRATION_DIR) -database "sqlite3://$(DB_URL)" -verbose down
create-migration:
	migrate create -ext sql -dir $(MIGRATION_DIR) -seq $(NAME)


.PHONY: all build run river test clean migrate-up migrate-down create-migration