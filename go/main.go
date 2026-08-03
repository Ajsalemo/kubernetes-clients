package main

import (
	"flag"
	"net/http"
	"path/filepath"

	"go.uber.org/zap"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/util/homedir"

	home "go-kubernetes-client/controllers"
)

func main() {
	var kubeconfig *string
	if home := homedir.HomeDir(); home != "" {
		kubeconfig = flag.String("kubeconfig", filepath.Join(home, ".kube", "config"), "(optional) absolute path to the kubeconfig file")
	} else {
		kubeconfig = flag.String("kubeconfig", "", "absolute path to the kubeconfig file")
	}
	flag.Parse()

	// use the current context in kubeconfig
	config, err := clientcmd.BuildConfigFromFlags("", *kubeconfig)
	if err != nil {
		panic(err.Error())
	}

	// create the clientset
	client, err := kubernetes.NewForConfig(config)
	if err != nil {
		panic(err.Error())
	}
	logger, _ := zap.NewProduction()
	defer logger.Sync()
	log := logger.Sugar()

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		home.HomeHandler(w, r, log)
	})

	http.HandleFunc("/pods/list/all", func(w http.ResponseWriter, r *http.Request) {
		home.GetAllPods(w, r, log, client)
	})
	// See https://stackoverflow.com/questions/15240884/how-can-i-handle-http-requests-of-different-methods-to-in-go
	// You can prefix HTTP verbs before the route
	http.HandleFunc("POST /deployment/create", func(w http.ResponseWriter, r *http.Request) {
		home.CreateDeploymentHandler(w, r, log, client)
	})

	http.HandleFunc("/pod/get", func(w http.ResponseWriter, r *http.Request) {
		home.GetPod(w, r, log, client)
	})
	// See https://stackoverflow.com/questions/15240884/how-can-i-handle-http-requests-of-different-methods-to-in-go
	// You can prefix HTTP verbs before the route
	http.HandleFunc("DELETE /pod/delete", func(w http.ResponseWriter, r *http.Request) {
		home.DeletePod(w, r, log, client)
	})

	log.Info("Starting server on :8080")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
