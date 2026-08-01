package controllers

import (
	"encoding/json"
	"net/http"

	"go.uber.org/zap"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
)

func GetAllPods(w http.ResponseWriter, r *http.Request, log *zap.SugaredLogger, client *kubernetes.Clientset) {
	query := r.URL.Query()
	namespace := query.Get("namespace")
	// If namespace is empty, default to "default"
	if namespace == "" {
		namespace = "default"
	}

	pods, err := client.CoreV1().Pods(namespace).List(r.Context(), metav1.ListOptions{})

	w.Header().Set("Content-Type", "application/json")
	if err != nil {
		log.Errorf("Error getting pods: %v", err)
		http.Error(w, "Error getting pods", http.StatusInternalServerError)
		return
	}

	if err := json.NewEncoder(w).Encode(pods.Items); err != nil {
		log.Errorf("Error encoding pods: %v", err)
		http.Error(w, "Error encoding pods", http.StatusInternalServerError)
		return
	}
}
