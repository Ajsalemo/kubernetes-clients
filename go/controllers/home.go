package controllers

import (
	"net/http"

	"go.uber.org/zap"
)

func HomeHandler(w http.ResponseWriter, r *http.Request, log *zap.SugaredLogger) {
	w.Write([]byte("kubernetes-client-go"))
}
