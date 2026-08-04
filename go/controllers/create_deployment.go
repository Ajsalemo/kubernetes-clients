package controllers

import (
	"encoding/json"
	"go-kubernetes-client/models"
	"net/http"

	"go.uber.org/zap"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/client-go/kubernetes"
)

func CreateDeploymentHandler(w http.ResponseWriter, r *http.Request, log *zap.SugaredLogger, client *kubernetes.Clientset) {
	// Implement the logic to create a deployment here
	// You can use the client to interact with the Kubernetes API
	// For example, you can create a deployment using the client.AppsV1().Deployments(namespace).Create() method
	query := r.URL.Query()
	namespace := query.Get("namespace")
	// If namespace is empty, default to "default"
	if namespace == "" {
		namespace = "default"
	}

	log.Info("Creating deployment...")
	// Add your deployment creation logic here
	decoder := json.NewDecoder(r.Body)

	var deploymentSpec models.Deployment
	if err := decoder.Decode(&deploymentSpec); err != nil {
		http.Error(w, "Invalid request payload", http.StatusBadRequest)
		return
	}
	log.Infof("Received deployment spec: %+v", deploymentSpec)

	pod := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      deploymentSpec.Metadata.Name,
			Namespace: namespace,
		},
		Spec: appsv1.DeploymentSpec{
			Replicas: int32Ptr(deploymentSpec.Spec.Replicas),
			Template: corev1.PodTemplateSpec{
				Spec: corev1.PodSpec{
					Containers: func() []corev1.Container {
						containers := []corev1.Container{}
						for _, v := range deploymentSpec.Spec.Template.Spec.Containers {
							containers = append(containers, corev1.Container{
								Name:  v.Name,
								Image: v.Image,
							})
						}
						return containers
					}(),
				},
			},
		},
	}

	_, err := client.AppsV1().Deployments(namespace).Create(r.Context(), pod, metav1.CreateOptions{})
	if err != nil {
		log.Errorf("Failed to create deployment: %v", err)
		http.Error(w, "Failed to create deployment", http.StatusInternalServerError)
		return
	}
	w.WriteHeader(http.StatusOK)
	w.Write([]byte("Deployment created successfully"))
}

func int32Ptr(i int32) *int32 {
	return &i
}
