package controllers

import (
	"encoding/json"
	"net/http"

	"go.uber.org/zap"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
)

func GetPod(w http.ResponseWriter, r *http.Request, log *zap.SugaredLogger, client *kubernetes.Clientset) {
	query := r.URL.Query()
	namespace := query.Get("namespace")
	name := query.Get("name")
	// If namespace is empty, default to "default"
	if namespace == "" {
		namespace = "default"
	}

	pod, err := client.CoreV1().Pods(namespace).Get(r.Context(), name, metav1.GetOptions{})

	w.Header().Set("Content-Type", "application/json")
	if err != nil {
		log.Errorf("Error getting pod: %v", err)
		http.Error(w, "Error getting pod", http.StatusInternalServerError)
		return
	}

	if err := json.NewEncoder(w).Encode(pod.Spec); err != nil {
		log.Errorf("Error encoding pod: %v", err)
		http.Error(w, "Error encoding pod", http.StatusInternalServerError)
		return
	}
}
