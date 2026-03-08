import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def listAllPods(v1, namespace, watch=False, all=False):
    # List all pods in all namespaces
    if all:
        logger.info(f" Listing all pods in the namespace: {namespace}")
        pods = v1.list_namespaced_pod(namespace, watch=watch)
    logger.info(f" Total pods found: {len(pods.items)}")
    for pod in pods.items:
        logger.info(f" {pod.metadata.namespace}\t{pod.metadata.name}")
