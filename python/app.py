import logging
import sys

from fastapi import FastAPI
from kubernetes import client, config
from pydantic import BaseModel

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class Resources(BaseModel):
    requests: dict | None = None
    limits: dict | None = None


class Container(BaseModel):
    name: str
    image: str
    resources: Resources | None = None


class Deployment(BaseModel):
    name: str
    namespace: str = "default"
    replicas: int = 1
    containers: list[Container]


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


@app.get("/pods/list/all/{namespace}")
def list_all_pods(namespace: str = "default"):
    logger.info(f" Listing all pods in the namespace: {namespace}")
    pods = v1.list_namespaced_pod(namespace, watch=watch)
    logger.info(f" Total pods found: {len(pods.items)}")

    if len(pods.items) > 0:
        for pod in pods.items:
            logger.info(f" {pod.metadata.namespace}\t{pod.metadata.name}")

        return {"message": f"Listed all pods in namespace: {namespace}"}
    else:
        logger.info(f" No pods found in namespace: {namespace}")
        return {"message": f"No pods found in namespace: {namespace}"}


@app.get("/pod/get/{pod}")
def get_pod(pod: str, namespace: str = "default"):
    logger.info(f" Getting pod {pod} in the namespace: {namespace}")
    try:
        pod_info = v1.read_namespaced_pod(pod, namespace)
        logger.info(
            f" Pod found: {pod_info.metadata.name} in namespace: {pod_info.metadata.namespace}")

        pod_info_dict = {
            "name": pod_info.metadata.name,
            "namespace": pod_info.metadata.namespace,
            "labels": pod_info.metadata.labels,
            "annotations": pod_info.metadata.annotations,
            "status": pod_info.status.phase,
            "spec": {
                "containers": [
                    {
                        "name": container.name,
                        "image": container.image,
                        "resources": {
                            "requests": container.resources.requests,
                            "limits": container.resources.limits
                        }
                    } for container in pod_info.spec.containers
                ]
            }
        }
        return {"message": f"Pod found: {pod_info.metadata.name} in namespace: {pod_info.metadata.namespace}", "pod_info": pod_info_dict}
    except client.exceptions.ApiException as e:
        if e.status == 404:
            logger.info(f" Pod '{pod}' not found in namespace: {namespace}")
            return {"message": f"Pod '{pod}' not found in namespace: {namespace}"}
        else:
            logger.error(
                f" Error retrieving pod '{pod}' in namespace: {namespace}: {e}")
            return {"message": f"Error retrieving pod '{pod}' in namespace: {namespace}: {e}"}


@app.delete("/pod/delete/{pod}")
def delete_pod(pod: str, namespace: str = "default"):
    logger.info(f" Deleting pod {pod} in the namespace: {namespace}")
    try:
        v1.delete_namespaced_pod(pod, namespace)
        logger.info(
            f" Pod '{pod}' deleted successfully in namespace: {namespace}")
        return {"message": f"Pod '{pod}' deleted successfully in namespace: {namespace}"}
    except client.exceptions.ApiException as e:
        if e.status == 404:
            logger.info(f" Pod '{pod}' not found in namespace: {namespace}")
            return {"message": f"Pod '{pod}' not found in namespace: {namespace}"}
        else:
            logger.error(
                f" Error deleting pod '{pod}' in namespace: {namespace}: {e}")
            return {"message": f"Error deleting pod '{pod}' in namespace: {namespace}: {e}"}


@app.post("/deployment/create")
def create_deployment(deployment: Deployment):
    logger.info(
        f" Creating deployment {deployment.name} in the namespace: {deployment.namespace}")
    try:
        # Define the deployment spec
        deployment_spec = client.V1Deployment(
            metadata=client.V1ObjectMeta(name=deployment.name),
            spec=client.V1DeploymentSpec(
                replicas=deployment.replicas,
                selector={'matchLabels': {'app': deployment.name}},
                template=client.V1PodTemplateSpec(
                    metadata=client.V1ObjectMeta(
                        labels={'app': deployment.name}),
                    spec=client.V1PodSpec(containers=[
                        client.V1Container(
                            name=c.name,
                            image=c.image,
                            resources=client.V1ResourceRequirements(
                                requests=c.resources.requests if c.resources else None,
                                limits=c.resources.limits if c.resources else None,
                            ) if c.resources else None,
                        ) for c in deployment.containers
                    ])
                )
            )
        )

        # Create the deployment
        apps_v1 = client.AppsV1Api()
        apps_v1.create_namespaced_deployment(
            namespace=deployment.namespace,
            body=deployment_spec
        )
    except client.exceptions.ApiException as e:
        logger.error(
            f" Error creating deployment '{deployment.name}' in namespace: {deployment.namespace}: {e}")
        return {"message": f"Error creating deployment '{deployment.name}' in namespace: {deployment.namespace}: {e}"}
    logger.info(
        f" Deployment '{deployment.name}' created successfully in namespace: {deployment.namespace}")
    return {"message": f"Deployment '{deployment.name}' created successfully in namespace: {deployment.namespace}"}
