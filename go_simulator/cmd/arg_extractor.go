package cmd

import (
	"encoding/json"
	"log"
	"os"
)

func getArgsFromFile(relativePath string) config {
	file, err := os.Open(relativePath)
	if err != nil {
		log.Fatalf("Erro ao abrir o arquivo: %v", err)
	}
	defer file.Close()

	args := config{}

	decoder := json.NewDecoder(file)
	err = decoder.Decode(&args)
	if err != nil {
		log.Fatalf("Erro ao decodificar o arquivo JSON: %v", err)
	}
	return args
}
