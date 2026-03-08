import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def listAllPods(v1):
    logger.info(" Listing all pods in the cluster:")
    pods = v1.list_pod_for_all_namespaces(watch=False)
    logger.info(f" Total pods found: {len(pods.items)}")
    for pod in pods.items:
        logger.info(f" {pod.metadata.namespace}\t{pod.metadata.name}")
