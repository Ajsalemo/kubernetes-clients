package main

import (
	"net/http"

	"go.uber.org/zap"

	home "go-kubernetes-client/controllers"
)

func main() {
	logger, _ := zap.NewProduction()
	defer logger.Sync()
	log := logger.Sugar()

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		home.HomeHandler(w, r, log)
	})

	log.Info("Starting server on :8080")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
