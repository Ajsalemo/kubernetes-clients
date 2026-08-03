package controllers

import (
	"encoding/json"
	"net/http"

	"go.uber.org/zap"
	"k8s.io/client-go/kubernetes"
)

func CreateDeploymentHandler(w http.ResponseWriter, r *http.Request, log *zap.SugaredLogger, client *kubernetes.Clientset) {
	// Implement the logic to create a deployment here
	// You can use the client to interact with the Kubernetes API
	// For example, you can create a deployment using the client.AppsV1().Deployments(namespace).Create() method

	log.Info("Creating deployment...")
	// Add your deployment creation logic here
	decoder := json.NewDecoder(r.Body)

	var deploymentSpec map[string]interface{}
	if err := decoder.Decode(&deploymentSpec); err != nil {
		http.Error(w, "Invalid request payload", http.StatusBadRequest)
		return
	}
	log.Infof("Received deployment spec: %+v", deploymentSpec)

	w.WriteHeader(http.StatusOK)
	w.Write([]byte("Deployment created successfully"))
}
