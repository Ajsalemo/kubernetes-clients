package controllers

import (
	"encoding/json"
	"net/http"

	"go.uber.org/zap"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
)

func DeletePod(w http.ResponseWriter, r *http.Request, log *zap.SugaredLogger, client *kubernetes.Clientset) {
	query := r.URL.Query()
	namespace := query.Get("namespace")
	name := query.Get("name")
	// If namespace is empty, default to "default"
	if namespace == "" {
		namespace = "default"
	}

	err := client.CoreV1().Pods(namespace).Delete(r.Context(), name, metav1.DeleteOptions{})

	w.Header().Set("Content-Type", "application/json")
	if err != nil {
		log.Errorf("Error deleting pod: %v", err)
		http.Error(w, "Error deleting pod", http.StatusInternalServerError)
		return
	}

	if err := json.NewEncoder(w).Encode(map[string]string{"message": "deleted"}); err != nil {
		log.Errorf("Error encoding response: %v", err)
		http.Error(w, "Error encoding response", http.StatusInternalServerError)
		return
	}
}
