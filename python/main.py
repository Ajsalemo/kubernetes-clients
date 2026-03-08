import argparse
import logging
import sys

from fastapi import FastAPI
from kubernetes import client, config

from src.listallpods import listAllPods

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

# load the kubeconfig
try:
    logger.info("Loading kubeconfig..")
    config.load_kube_config()
    logger.info("kubeconfig loaded successfully.")
except config.ConfigException as e:
    logger.error("Failed to load kubeconfig: %s", e)
    sys.exit(1)

# create the api client
# pass this in to any functions
v1 = client.CoreV1Api()
# This program can be ran as a CLI tool or as a web server
# If ran through a server like gunicorn/uvicorn, app the annotated @app routes will be invoked - main() will not be invoked
# If ran through the CLI, main() will be invoked and the @app routes will not be invoked

@app.get("/pods/list/all/{namespace}")
def read_item(namespace: str = "default"):
    listAllPods(v1, namespace)
    return {"message": f"Listed all pods in namespace: {namespace}"}


def main():
    # list all pods
    pod_list_all = argparse.ArgumentParser(description="x")
    pod_list_all.add_argument(
        "pod", help="x"
    )
    pod_list_all.add_argument(
        "list", help="x"
    )
    pod_list_all.add_argument(
        "--namespace", type=str, default="default", help="Namespace to list pods from"
    )
    pod_list_all.add_argument(
        "--all", type=bool, default=False, help="List all pods from the specified namespace. Namespace is 'default' if not specified."
    )    
    pod_list_all.add_argument(
        "--watch", type=bool, default=False, help="Watch for changes in the pods"
    )    
    pod_list_all_args = pod_list_all.parse_args()

    # list all pods in the cluster
    if pod_list_all_args.pod == "pod" and pod_list_all_args.list == "list":
        listAllPods(v1, pod_list_all_args.namespace, pod_list_all_args.watch, pod_list_all_args.all)

if __name__ == "__main__":
    main()
